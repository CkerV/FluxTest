# 前言
* 在一次日常逛知乎的时候发现了Android Flux，相比与之前的开发，自己还从未接触到这样的开发模式，于是经过查阅资料后，自己动手尝试体验了一番，并加以总结记录。
 <!--more-->


# 简介
* Flux源于Facebook在14年提出的一种Web前端架构，主要是用来处理复杂的UI逻辑的一致性问题。经过实践后发现，这种架构可以很好的应用于Android平台，相对于其他的MVC/MVP/MVVM等模式，拥有良好的文档和更具体的设计，比较适合于快速开发实现。

# 核心思想
## **单向数据流**

![Android Flux](http://upload-images.jianshu.io/upload_images/653561-81d4a669d3f691b5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

* 如图所示，Flux的核心思想是单向的数据流，所谓单向数据流，就是当用户进行操作的时候，会从View层发出一个Action，这个Action通过Dispatcher流向Store里面，触发Store对状态进行改动，然后再由Store触发新的状态通知到View进行重新渲染。

## **Action**
* 用户操作界面时会触发一个Action，这个Action即是数据的封装，Dispatcher会将这个Action分发到Store
* Action的创建一般被封装到一个有语义的Helper类，（**ActionCreator**），所谓有语义，就是根据不同的业务创建不同的Action，然后把这个Action传递到Dispatcher，进行Action的分发

## **Dispatcher**
* 一个应用中只有一个Dispatcher，Dispatcher管理所有的数据流，（即内部管理的是所有的Store），它是Action与Store联系的中心枢纽
* 实际上，它管理的是Store注册的一系列回调接口，本身没有其他的逻辑，仅仅是把Action分发到Store
* 由此可知，所有的数据流必须从这里经过，依次分发到已注册的Store中

## **Store**
* Store包含应用的状态和逻辑，通常在此类中实现对Action的逻辑处理，并把处理的结果通知到View中
* Store会把自己注册在Dispatcher上并提供一个回调的接口，用于处理Action，当Dispatcher分发Action时，内部管理的Store就会回调这个用于处理Action的接口
* 通过Dispatcher发送Action来更新Store的内部状态，当Store更新后，它会发送一个事件声明自己的状态已经发送了改变，（通常是View接收），然后View会读取这些变化并更新自己

## **View**
* 即是Activity和Fragment，负责监听Store发送的事件并更新界面
* 通常一个Activity对应一个Store，但是如果Activity包含许多Fragment，也可以让每个Fragment对应自己的Store

# 代码实践
* 本项目模拟了一个登陆的过程，Activity由两个EditText和一个Button组成，当username和password均为123时，模拟登陆成功；否则模拟登陆失败。

![Flux Test](http://img.blog.csdn.net/20161205124116265)

## Action
```java
/**
 * 简单的POJO类型，只提供两个字段：type 和 data, 分别记录Action的类型和数据
 * Created by ckerv on 16/12/4.
 */
public class Action<T> {

    private String type;

    private T data;

    public Action(String type, T data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
```
```java
/**
 * Created by ckerv on 16/12/5.
 */
public class LoginAction extends Action<LoginBean> {

    public static final String LOGIN_ACTION = "login.action";

    public LoginAction(String type, LoginBean data) {
        super(type, data);
    }
}
```
* Action是简单的POJO类型，采用泛型对数据进行封装，只提供了两个字段 `type` 和 `data` ，分别记录Action的类型和数据
* LoginAction是Action的具体业务实现，实现非常简单，只添加了一个Action类型字段 `LOGIN_ACTION`

## Dispatcher
```java
/**
 * 一个app通常只有一个Dispatcher类,内部进行对Store的管理
 * 此类的作用是将action分发到store,是连接action和store的中心枢纽
 * Created by ckerv on 16/12/4.
 */
public class Dispatcher {

    private static Dispatcher INSTANCE;

    private List<Store> mStores;

    private Dispatcher() {
        mStores = new ArrayList<>();
    }

    public static Dispatcher getInstance() {
        if(INSTANCE == null) {
            synchronized (Dispatcher.class) {
                if(INSTANCE == null) {
                    INSTANCE = new Dispatcher();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 注册store,即把store添加到list中
     * @param store
     */
    public void register(Store store) {
        this.mStores.add(store);
    }

    /**
     * 注销store,从list中移除
     * @param store
     */
    public void unRegister(Store store) {
        this.mStores.remove(store);
    }

    /**
     * 分发action
     * @param action
     */
    public void dispatch(Action action) {
        post(action);
    }

    /**
     * 向注册的store list依次分发acion
     * @param action
     */
    private void post(Action action) {
        for (Store store : mStores) {
            store.onAction(action);
        }
    }
}
```
* Store会在这里注册自己的回调接口，Dispatcher会把Action分发到注册的Store，所以它会提供一些公有方法来注册监听和分发消息
* Dispatcher对外仅暴露3个公有方法：
	* `register(final Store store)` 用来注册每个Store的回调接口
	* `unregister(final Store store)` 用来接触Store的回调接口
	* `dispatch(Action action)` 用来触发Store注册的回调接口
* 用一个ArrayList来管理Stores,对于一个更复杂的app可能需要精心设计store所管理的数据结构

## Store
```java
/**
 * 处理action的基类,通常可根据不同的业务逻辑实现{@link #onAction(Action)}
 * 通过EventBus传递数据
 * Created by ckerv on 16/12/4.
 */
public abstract class Store {

    private static final EventBus mBus = EventBus.getDefault();

    protected Store() {

    }

    public void register(Object view) {
        this.mBus.register(view);
    }

    public void unRegister(Object view) {
        this.mBus.unregister(view);
    }

    /**
     * post事件到view层,进行UI的后续处理
     */
    protected void emitStoreChange() {
        this.mBus.post(changeEvent());
    }


    /**
     * 处理Action的逻辑,子类必须实现此方法
     * @param action
     */
    public abstract void onAction(Action action);

    /**
     * post事件
     * @return
     */
    protected abstract StoreChangeEvent changeEvent();

    public class StoreChangeEvent{}
}
```
* 这里Store是个抽象类，处理具体业务逻辑的子类必须实现`onAction(Action action)`和`changeEvent()`方法
* 采用EventBus来向view发送事件，因此必须提供`register(Object view)`和`unRegister(Object view)`方法给外部注册和注销EventBus

## LoginStore
```java
/**
 * Created by ckerv on 16/12/5.
 */
public class LoginStore extends Store {

    public LoginStore() {
        super();
        loginResponseBean = new LoginResponseBean();
    }

    private LoginResponseBean loginResponseBean;

    public LoginResponseBean getLoginResponseBean() {
        return loginResponseBean;
    }

    @Override
    public void onAction(Action action) {
        switch (action.getType()) {
            case LoginAction.LOGIN_ACTION:
                LoginBean loginBean = (LoginBean) action.getData();
                if(loginBean.getUserName().equals("123") && loginBean.getPassWord().equals("123")) {
                    loginResponseBean.setSuccess(true);
                } else {
                    loginResponseBean.setSuccess(false);
                }
                break;
            default:break;
        }
        emitStoreChange();
    }

    @Override
    protected StoreChangeEvent changeEvent() {
        return new StoreChangeEvent();
    }
}
```
* 处理登录逻辑的LoginStore类，继承自Store，可以看到，在`onAction(Action)`进行登录逻辑的验证
* 在登录逻辑的处理过程中，通过改变`LoginReponseBean`的状态来区分登录成功与否，处理完逻辑之后，调用父类的`emitStoreChange()`方法发送事件通知view更新
* 请注意，这里只提供LoginReponseBean的`get`方法，Store内部状态的更新只能是通过Dispatcher改变

## MainActivity
```java
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mEtUsername;
    private EditText mEtPassword;
    private Button mBtnLogin;

    private ActionCreator mActionCreator;
    private Dispatcher mDispatcher;
    private LoginStore mStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initVariables();
        initViews();

    }

    private void initVariables() {
        mStore = new LoginStore();
        mDispatcher = Dispatcher.getInstance();
        mActionCreator = ActionCreator.getInstance(mDispatcher);
        mDispatcher.register(mStore);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mStore.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStore.unRegister(this);
    }

    private void initViews() {

        mEtUsername = (EditText) findViewById(R.id.main_et_username);
        mBtnLogin = (Button) findViewById(R.id.main_btn_login);
        mEtPassword = (EditText) findViewById(R.id.main_et_password);
        mBtnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_btn_login :
                mActionCreator.login(mEtUsername.getText().toString(), mEtPassword.getText().toString());
                break;
            default : break;
        }
    }

    @Subscribe
    public void onChangeEvent(Store.StoreChangeEvent changeEvent) {
       if(mStore.getLoginResponseBean().isSuccess()) {
           Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_LONG).show();
       } else {
           Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_LONG).show();
       }
    }
}

```
* 一般说来，一个Activity有多少个业务就有多少个Store
* 在`initVariables()`进行Store、Dispatcher和ActionCreator的初始化和注册
* 在`onResume()`和`onDestroy()`方法里进行EventBus的注册和注销
* 当用户点击登录按钮时，由ActionCreator调用具体的业务方法`login(String username， String password)`使Dispatcher分发LoginAction到LoginStore，当LoginStore处理完LoginAction发送事件到MainActivity，MainActivity层接收到事件，获取LoginStore的状态，更新界面。

## 流程总结
![流程总结](http://img.blog.csdn.net/20161205135241375)

# 小结
* 以上是我对Android Flux开发模式的第一次实践，可以看到，采用Android Flux的开发模式有如下好处：
  * View层只负责渲染界面和触发Action，具体业务逻辑由Store实现，高度解耦
  * 要理解一个Store可能发生的状态变化，只看`onAction(Action action)`中的逻辑处理即可
  * 由于数据(Action)是单向流动，因此Debug的时候变得轻松很多，可以快速定位Bug的发生地点
* 以上是个人对于Anroid Flux的理解，有不足之处望指出一起讨论，一起学习~O(∩_∩)O哈哈~

# 关于Android Flux
* [HelloWorld快速入门](http://androidflux.github.io/docs/overview.html#content)
* [AndroidFlux项目一览-Flux架构的Android移植](http://www.jianshu.com/p/896ce1a8e4ed)
* [Android Flux项目](http://androidflux.github.io/docs/overview.html#content)