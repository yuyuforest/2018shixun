# 安卓开发实验报告（第14周）

开始日期  2018/12/05  完成日期  2018/12/19

------

## 一、实验题目

1. 学会使用HttpURLConnection请求访问Web服务
2. 学习Android线程机制，学会线程更新UI
3. 学会解析JSON数据
4. 学习CardView布局技术

## 二、实验内容

### 要求

- 搜索框只允许正整数int类型，不符合的需要弹Toast提示
- 当手机处于飞行模式或关闭wifi和移动数据的网络连接时，需要弹Toast提示
- 由于bilibili的API返回状态有很多，这次我们特别的限制在以下几点
  - 基础信息API接口为： `https://space.bilibili.com/ajax/top/showTop?mid=<user_id>`
  - 图片信息API接口为基础信息API返回的URL，cover字段
  - 只针对前40的用户id进行处理，即`user_id <= 40`
  - [2,7,10,19,20,24,32]都存在数据，需要正确显示
- **在图片加载出来前需要有一个加载条，不要求与加载进度同步**
- 布局和样式没有强制要求，只需要展示图片/播放数/评论/时长/创建时间/标题/简介的内容即可，可以自由发挥
- **布局需要使用到CardView和RecyclerView**
- 每个item最少使用2个CardView，布局怎样好看可以自由发挥，不发挥也行
- 不完成加分项的同学可以不显示SeekBar
- 输入框以及按钮需要一直处于顶部

### 验收内容

1. 图片/播放数/评论/时长/创建时间/标题/简介 显示是否齐全正确，
2. 是否存在加载条
3. Toast信息是否准确，特别地，针对用户网络连接状态和数据不存在情况的Toast要有区别
4. 多次搜索时是否正常
5. 代码+实验报告
6. 好看的界面会酌情加分，不要弄得像demo那么丑= =

## 三、实验结果

### （1）实验截图

- 正常搜索

![](images/week14-search.png)

- 网络无连接

![](images/week14-no-net.png)

- 记录不存在

![](images/week14-no-record.png)

- 查看预览图

![](images/week14-preview.png)

- 不存在预览图时，不显示进度条

  ![](images/week14-no-frame.png)


### （2）实验步骤以及关键代码

#### 数据类型

`model.bili.VideoJson` 要请求/解析的视频概要信息的数据结构

`model.bili.FramesJson` 要请求/解析的预览帧信息的数据结构

`model.bili.PreviewInfo` 要显示的视频卡片的数据结构

#### RecyclerView控制

`ui.bili.PreviewHolder` 视频卡片的 `ViewHolder`

`ui.bili.PreviewAdapter` 视频卡片列表的适配器

#### 网络请求

发起网络请求的被观察者所在的方法： `ui.bili.BiliHandler#beginRequest`

```java
        // 被观察者负责请求网络获取信息
        Observable<Object[]> observable = Observable.create(new ObservableOnSubscribe<Object[]>() {
            @Override
            public void subscribe(ObservableEmitter<Object[]> emitter) {
                try {
                    // 请求视频信息
                    InputStream is = Utils.requestURL("https://space.bilibili.com/ajax/top/showTop?mid=" + id);

                    // 解析视频信息
                    String jsonstr = Utils.readStringFromInputStream(is);
                    VideoJson videoJson = new Gson().fromJson(jsonstr, VideoJson.class);
                    if(videoJson.getStatus()) {
                        // 通知观察者更新视频信息
                        Object[] info = new Object[]{RequestType.INFO, videoJson};
                        emitter.onNext(info);

                        // 请求封面
                        is = Utils.requestURL(videoJson.getData().getCover());

                        // 封面和帧图
                        Bitmap cover = Utils.readBitmapFromInputStream(is);
                        ArrayList<Bitmap> frames = null;

                        // 请求视频帧信息
                        is = Utils.requestURL("https://api.bilibili.com/pvideo?aid=" + videoJson.getData().getAid());
                        String jsonstr2 = Utils.readStringFromInputStream(is);
                        FramesJson framesJson = new Gson().fromJson(jsonstr2, FramesJson.class);
                        FramesJson.Data framesData = framesJson.getData();
                        int count = framesData.getCount();

                        if(framesJson.getData().getImage() != null && framesJson.getData().getImage().length > 0) {
                            // 请求帧图
                            is = Utils.requestURL(framesJson.getData().getImage()[0]);
                            frames = Utils.readFramesFromInputStream(is, count, framesData.getImg_x_len(), framesData.getImg_y_len(),
                                    framesData.getImg_x_size(), framesData.getImg_y_size());

                        }
                        // 通知观察者更新封面和帧图
                        Object[] image = new Object[]{RequestType.IMAGE, cover, frames};
                        emitter.onNext(image);
                    }
                    else {
                        emitter.onNext(new Object[]{RequestType.ERROR, "该记录不存在"});
                    }
                } catch (JsonSyntaxException e) {
                    emitter.onNext(new Object[]{RequestType.ERROR, "该记录不存在"});
                } catch (SocketTimeoutException e) {
                    emitter.onNext(new Object[]{RequestType.ERROR, "网络连接异常"});
                } catch (IOException e) {
                    emitter.onNext(new Object[]{RequestType.ERROR, "网络连接异常"});
                } finally {
                    emitter.onComplete();
                }
            }
        });
```

#### 更新UI

负责更新UI的观察者所在的方法： `ui.bili.BiliHandler#beginRequest`

```java
        // 观察者负责更新UI
        Observer<Object[]> observer = new Observer<Object[]>() {
            private Disposable disposable;
            private int pos = -1;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Object[] objs) {
                RequestType requestType = (RequestType) objs[0];
                switch (requestType) {
                    case INFO:
                        pos = onSearchResult((VideoJson)objs[1]);
                        break;
                    case IMAGE:
                        onCoverResult(pos, (Bitmap) objs[1], (ArrayList<Bitmap>)objs[2]);
                        break;
                    case ERROR:
                        Utils.toast(mActivity, (String)objs[1]);
                        break;
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        };
```

观察者在 `onNext` 中根据参数内容，调用以下方法更新UI：

```java
    public int onSearchResult(VideoJson videoJson) {
        if(videoJson.getStatus()) {
            PreviewInfo previewInfo = new PreviewInfo(videoJson);
            return mPreviewAdapter.add(previewInfo);
        } else {
            Utils.toast(mActivity, "该记录不存在");
            return -1;
        }
    }

    public void onCoverResult(int pos, Bitmap cover, ArrayList<Bitmap> frames) {
        mPreviewAdapter.update(pos, cover, frames);
    }
```

### （3）实验遇到的困难以及解决思路

#### 无法显示toast

在被观察者的 `subscribe` 方法中，我设置当捕获到异常时，弹出toast显示相关的异常信息。

但是当搜索不存在的记录或网络无连接时，没有弹出异常信息。

我猜测是因为被观察者不运行在安卓的UI线程上，所以无法执行UI相关的操作，不能弹出toast。

我把弹出toast的代码移到观察者的 `onNext` 方法中执行，就成功了。

#### Cleartext HTTP traffic to ... not permitted

在API 28的设备上运行时，会出现这样的报错。而API 26的设备上无此错误。

博客 [Android高版本联网失败报错:Cleartext HTTP traffic to xxx not permitted解决方法](https://blog.csdn.net/gengkui9897/article/details/82863966) 指出了原因：

> 为保证用户数据和设备的安全，Google针对下一代 Android 系统(Android P) 的应用程序，将要求默认使用加密连接，这意味着 Android P 将禁止 App 使用所有未加密的连接，因此运行 Android P 系统的安卓设备无论是接收或者发送流量，未来都不能明码传输，需要使用下一代(Transport Layer Security)传输层安全协议，而 Android Nougat 和 Oreo 则不受影响。
>
> 因此在Android P 使用HttpUrlConnection进行http请求会出现以下异常
>
>  `W/System.err: java.io.IOException: Cleartext HTTP traffic to **** not permitted`
>
> 使用OKHttp请求则出现
>
> `java.net.UnknownServiceException: CLEARTEXT communication ** not permitted by network security policy`

要解决这一问题，可以使用 `HttpsURLConnection` 代替 `HttpURLConnection` 。由于使用 `HttpsURLConnection` 需要安全证书，比较麻烦，所以我通过在 `AndroidManifest.xml` 的 `application` 标签中添加属性 `android:usesCleartextTraffic="true"` 来解决。

## 四、实验思考及感想

1. 更深地体会了RxJava的使用。在 `Observable<T>.subscribe` 方法中，可以多次使用 `emiiter.onNext(T)` 来通知观察者，在最后调用 `emitter.onComplete()` 来完成事件。
2. 学习到了如何使用 `HttpURLConnection` 来发起网络请求，并读取、解析它返回的json字符串或图片。
3. 学习到了 `CardView` 的使用。一开始我不明白怎么在它里面进行复杂的布局，经查询， 原来是要在 `CardView` 里添加一个布局元素如 `ConstraintLayout` 、 `RelativeLayout` 之类，再将这个元素作为实际的根布局进行布局。

# 安卓开发实验报告（第15周）

## 一、实验题目

1. 理解Restful接口
2. 学会使用Retrofit2
3. 复习使用RxJava
4. 学会使用OkHttp

## 二、实验内容

### 基础实验内容

- 教程位于`./manual/tutorial_retrofit.md`
- 每次点击搜索按钮都会清空上次搜索结果再进行新一轮的搜索
- 获取repos时需要处理以下异常：HTTP 404 以及 用户没有任何repo
- 只显示 has_issues = true 的repo（即fork的他人的项目不会显示）
- repo显示的样式自由发挥，显示的内容可以自由增加（不能减少）
- repo的item可以点击跳转至下一界面
- 该repo不存在任何issue时需要弹Toast提示
- 不完成加分项的同学只需要显示所有issues即可，样式自由发挥，内容可以增加

### 加分项

- 加分项旨在学习Retrofit2的POST请求，加深Rest API的理解
- demo需要你们额外提供TOKEN参数，这点不必实现，实现了反而不利于检查
- 提交的代码可以删除掉你的token等授权信息
- 仅处理提交成功与提交失败两种状态
- issue提交成功后需要刷新展示出来

## 三、实验结果

### （1）实验截图

- 不存在的用户

  ![](images/week15-invalid-user.png)

- 存在的用户，正常显示仓库

  ![](images/week15-repos.png)

- 点击fork自他人的仓库

  ![](images/week15-fork.png)

- 点击仓库查看issues（动图）

  ![](images/week15-issues.gif)

- 创建issue（动图）

  ![](images/week15-create-issue.gif)

- 没有issues的仓库

  ![](images/week15-no-issues.png)

### （2）实验步骤以及关键代码

#### 数据类型

`model.github.RepoModel` repository信息的数据结构

`model.github.IssueModel` issue信息的数据结构

`model.github.IssuePost` POST过去的要创建的新issue的数据结构

#### RecyclerView控制

`ui.github.repos.RepoHolder` 仓库列表项的 `ViewHolder`

`ui.github.repos.RepoAdapter` 仓库列表的适配器

`ui.github.issues.IssueHolder` issues列表项的 `ViewHolder`

`ui.github.issues.IssueAdapter` issues列表的适配器

#### Retrofit网络请求接口

```java
public interface GithubService {
    String baseURL = "https://api.github.com";

    @GET("/users/{user_name}/repos")
    Observable<List<RepoModel>> getRepos(@Path("user_name") String user_name);


    @GET("/repos/{user_name}/{repo_name}/issues")
    Observable<List<IssueModel>> getIssues(@Path("user_name") String user_name, @Path("repo_name") String repo_name);

    @Headers("Authorization: token bd6d7c38932fc404f415b4588c31bf83178c0bd2")
    @POST("/repos/{user_name}/{repo_name}/issues")
    Observable<IssueModel> newIssue(@Path("user_name") String user_name, @Path("repo_name") String repo_name, @Body IssuePost issuePost);
}
```

#### 请求repository                                                                                                                                                                                                                                                                                                                                                                                             

在 `ui.github.repos.GithubReposHandler` 中进行对仓库列表的请求。

```java
    public void requestRepos() {
        String username = inputUser.getText().toString();

        OkHttpClient build = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GithubService.baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(build)
                .build();

        GithubService githubService = retrofit.create(GithubService.class);

        Observable<List<RepoModel>> observable = githubService.getRepos(username);

        Observer<List<RepoModel>> observer = new Observer<List<RepoModel>>() {
            private Disposable disposable;
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(List<RepoModel> repoModels) {
                onSearchResult(repoModels);
                onComplete();
            }

            @Override
            public void onError(Throwable e) {
                if(e instanceof HttpException && ((HttpException) e).code() == 404) {
                    Utils.toast(mActivity, "该用户不存在");
                } else if(e instanceof UnknownHostException) {
                    Utils.toast(mActivity, "网络连接异常");
                } else {
                    Utils.toast(mActivity, e.getMessage());
                }
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        };

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
```

#### 请求issues

在 `ui.github.issues.GithubIssuesHandler` 中进行对issues列表的请求。

```java
    public void requestIssues() {
        OkHttpClient build = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GithubService.baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(build)
                .build();

        GithubService githubService = retrofit.create(GithubService.class);

        Observable<List<IssueModel>> observable = githubService.getIssues(username, reponame);

        Observer<List<IssueModel>> observer = new Observer<List<IssueModel>>() {
            private Disposable disposable;
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(List<IssueModel> issueModels) {
                loadIssues(issueModels);
                onComplete();
            }

            @Override
            public void onError(Throwable e) {
                if(e instanceof HttpException) {
                    Utils.toast(mActivity, "该仓库不存在");
                } else if(e instanceof UnknownHostException) {
                    Utils.toast(mActivity, "网络连接异常");
                } else {
                    Utils.toast(mActivity, e.getMessage());
                }
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        };

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
```

#### 创建新issue

在 `ui.github.issues.GithubIssuesHandler` 中创建新的issue。

```java
    public void submit() {
        OkHttpClient build = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GithubService.baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(build)
                .build();

        GithubService githubService = retrofit.create(GithubService.class);

        String title = inputTitle.getText().toString();
        String body = inputBody.getText().toString();
        inputTitle.setText("");
        inputBody.setText("");

        Observable<IssueModel> observable = githubService.newIssue(username, reponame, new IssuePost(title, body));

        Observer<IssueModel> observer = new Observer<IssueModel>() {
            private Disposable disposable;
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(IssueModel issueModel) {
                mIssueAdapter.add(issueModel);
                onComplete();
            }

            @Override
            public void onError(Throwable e) {
                if(e instanceof UnknownHostException) {
                    Utils.toast(mActivity, "网络连接异常");
                } else {
                    Utils.toast(mActivity, e.getMessage());
                }
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        };

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
```

### （3）实验遇到的困难以及解决思路

#### Unable to create call adapter for rx.Observable

这个问题的原因是 `build.gradle` 中依赖的错误。`build.gradle` 中 `retorfit` 或 `rxjava` 的版本一定要一致。

事实上，`retrofit` 兼容了 `rxjava` 和 `rxjava2` 。这次错误大概在于依赖使用了从教程拷贝过来的 `com.squareup.retrofit2:adapter-rxjava:x.x.x` 而非 `com.squareup.retrofit2:adapter-rxjava2:x.x.x` 。这个错误很细微，要小心。

## 四、实验思考及感想

1. 在 `TextView.setText("xxx:" + xxx)` 的动态赋值的代码处， IDE建议：`Do not concatenate text displayed with 'setText'. Use resource string with placeholders.` 。即是说，不应该直接在 `TextView.setText` 方法的参数里拼接字符串，而应该使用有占位符的资源字符串来控制格式。这样可以避免不能正确地转译字符串信息。

   所以，在 `res/values/strings.xml` 定义如下，其中， `%n` 表示第n个参数， `$d` 表示整数， `$s` 表示字符串（又： `$f` 表示浮点数）：

```xml
<resources>
    <string name="app_name">项目5</string>
    <string name="bili_play">播放：%1$d</string>
    <string name="bili_review">弹幕：%1$d</string>
    <string name="bili_duration">时长：%1$s</string>
    <string name="bili_create">发表时间：%1$s</string>
    <string name="repo_name">仓库：%1$s</string>
    <string name="repo_id">仓库id：%1$d</string>
    <string name="issues_num">issues数：%1$d</string>
    <string name="repo_description">仓库描述：%1$s</string>
    <string name="issue_title">问题：%1$s</string>
    <string name="issue_create">创建时间：%1$s</string>
    <string name="issue_state">问题状态：%1$s</string>
    <string name="issue_body">问题描述：%1$s</string>
</resources>
```

​	动态赋值应当类似如下代码：

```java
play.setText(context.getString(R.string.bili_play, info.getPlay()));
```

2. 学习到了Retrofit的一些用法。Retrofit的使用流程大致是：

   1. 添加Retrofit库的依赖和网络权限。
   2. 创建接收服务器返回数据的类。
   3. 创建用于描述网络请求的接口，可以使用Retrofit提供的注解来描述、配置网络请求的参数。一个Http请求可以抽象成该接口中的一个方法，返回值就是封装好的网络请求。
   4. 创建Retrofit实例。视情况需要而指定数据解析器（比如解析json），和网络请求适配器（比如适配RxJava）。
   5. `Retrofit.create(...)` 创建网络请求接口的实例。调用该接口中的方法获取封装好的网络请求。
   6. 调用封装好的网络请求的方法，发送网络请求。
   7. 处理返回的数据。

3. 比起前一周的直接操作 `HttpURLConnection` 读取返回数据、解析，使用注解配置网络请求、封装了网络请求、有数据解析器与网络请求适配器的Retrofit显然更为便捷。

4. 经查询， `@Headers` 和 `@Header` 的用法有所区别。

   `@Headers` 用于添加固定的请求头，注解于方法之前；`@Header` 用于添加不固定的请求头，注解于方法的参数之前。如下：

   ```java
   // @Header
   @GET("user")
   Call<User> getUser(@Header("Authorization") String authorization)
   
   // @Headers
   @Headers("Authorization: authorization")
   @GET("user")
   Call<User> getUser()
   ```

   由于本次实验使用的token是固定的，所以我选择直接在方法前注解 `@Headers("Authorization: token ...")` 。