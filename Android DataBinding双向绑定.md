未完待续

### 单向绑定
当我们改变数据的时候，UI可以自动发生改变。

举一个EditText的例子

定义MyDataViewModel类继承ViewModel，使用可观察的变量提供可观察的数据。
```
class MyDataViewModel : ViewModel() {

    val name: ObservableField<String> = ObservableField()

    fun changeName() {
        name.set(name.get() + "_dmw")
    }

}
```
在布局文件中使用，将EditText的`text`属性绑定到viewModel的`name`属性
```
<data>
        <variable
            name="viewModel"
            type="com.example.android.databinding.twowaysample.data.MyDataViewModel" />
    </data>

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".BlogDemoActivity">

        <EditText
            android:id="@+id/etText"
            android:layout_width="0dp"
            android:layout_height="48dp"
            android:layout_marginTop="40dp"
            android:background="#bdbdbd"
            android:gravity="center"
            android:text="@{viewModel.name}"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

         <Button
            android:id="@+id/btnChangeViewModel"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:onClick="@{()->viewModel.changeName()}"
            android:text="改变ViewModel的name属性"
            android:textAllCaps="false"
            app:layout_constraintTop_toBottomOf="@+id/etText" />

    </androidx.constraintlayout.widget.ConstraintLayout>
```
在activity中初始化`MyDataViewModel`对象并赋值给布局文件的viewModel变量。
```
override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this).get(MyDataViewModel::class.java)

        val binding: ActivityBlogDemoBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_blog_demo)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }
```

```
fun changeName() {
    name.set(name.get() + "_dmw")
}
```
我们调用MyDataViewModel的changeName方法改变`_name`的值，UI会自动跟着改变。但是如果我们直接改变EditText的`text`，那么对应的MyDataViewModel的`_name`的值会发生改变吗？我们试一试：

```
binding.btnChangeText.setOnClickListener {
        binding.etText.setText("Hello world")

        Log.d(TAG, "onCreate: binding.etText.text = " + binding.etText.text)
        Log.d(TAG, "onCreate: viewModel.name = " + viewModel.name.value)
}
```
输出结果可以看到，那么对应的MyDataViewModel的`_name`的值并不会发生变化。
```
D/BlogDemoActivity: onCreate: binding.etText.text = Hello world
D/BlogDemoActivity: onCreate: viewModel.name = Ada
```
我们如何实现当UI发生变化的时候，自动映射到数据变化呢？使用双向绑定。

### 双向绑定

当我们改变数据的时候，UI可以自动发生改变。当UI发生变化的时候，相应的数据也会发生改变。

数据绑定框架已经为[常用的双向绑定属性](https://developer.android.com/topic/libraries/data-binding/two-way#two-way-attributes) 和属性改变监听器提供了双向绑定的实现。`android:text`属性就是其中之一。下面我们使用`android:text`属性的双向绑定。修改EditText：

```
<EditText
            android:id="@+id/etText"
            android:layout_width="0dp"
            android:layout_height="48dp"
            android:layout_marginTop="40dp"
            android:background="#bdbdbd"
            android:gravity="center"
            android:text="@={viewModel.name}"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent" />
```
注意变化的地方，使用的是`@={}`
```
android:text="@={viewModel.name}"
```
现在我们再直接改变EditText的`text`，看看对应的MyDataViewModel的`name`的值会是否会发生改变。
```
D/BlogDemoActivity: onCreate: binding.etText.text = Hello world
D/BlogDemoActivity: onCreate: viewModel.name = Hello world
```
可以看到，当UI发生变化当时候，数据也会跟着改变，这样就实现了双向绑定的功能。我们看一看[TextViewBindingAdapter.java](https://android.googlesource.com/platform/frameworks/data-binding/+/refs/heads/studio-master-dev/extensions/baseAdapters/src/main/java/androidx/databinding/adapters/TextViewBindingAdapter.java)的实现。

```
public class TextViewBindingAdapter {
    
    private static final String TAG = "TextViewBindingAdapters";


    @BindingAdapter("android:text")
    public static void setText(TextView view, CharSequence text) {
        final CharSequence oldText = view.getText();
        Log.d(TAG, "setText: oldText = " + oldText + " , text = " + text);
        if (text == oldText || (text == null && oldText.length() == 0)) {
            return;
        }
        if (text instanceof Spanned) {
            if (text.equals(oldText)) {
                return; // No change in the spans, so don't set anything.
            }
        } else if (!haveContentsChanged(text, oldText)) {
            return; // No content changes, so don't set anything.
        }
        //注释1处，只有Text发生了变化，才会执行setText方法
        view.setText(text);
    }

    @InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
    public static String getTextString(TextView view) {
        String s = view.getText().toString();
        Log.d(TAG, "getTextString: s = " + s);
        return s;
    }


    private static boolean haveContentsChanged(CharSequence str1, CharSequence str2) {
        if ((str1 == null) != (str2 == null)) {
            return true;
        } else if (str1 == null) {
            return false;
        }
        final int length = str1.length();
        if (length != str2.length()) {
            return true;
        }
        for (int i = 0; i < length; i++) {
            if (str1.charAt(i) != str2.charAt(i)) {
                return true;
            }
        }
        return false;
    }

}
```
首先定义了一个绑定适配器方法，当我们将数据绑定到EditText的android:text属性，会调用这个方法为`text`赋值。
```
@BindingAdapter("android:text")
public static void setText(TextView view, CharSequence text) {
        final CharSequence oldText = view.getText();
        Log.d(TAG, "setText: oldText = " + oldText + " , text = " + text);
        if (text == oldText || (text == null && oldText.length() == 0)) {
            return;
        }
        if (text instanceof Spanned) {
            if (text.equals(oldText)) {
                return; // No change in the spans, so don't set anything.
            }
        } else if (!haveContentsChanged(text, oldText)) {
            return; // No content changes, so don't set anything.
        }
       //注释1处，
        view.setText(text);
    }

```
**注意，我们是在经过一系列的判断之后，当tex发生变化的时候才调用EditText的setText方法。这是必须要注意的，否则会造成死循环。**

然后定义了getTextString方法并使用了@InverseBindingAdapter注解，注解的属性是attribute = "android:text"，事件是"android:textAttrChanged"。当为EditText设置android:text属性的时候这个方法会被调用，并且会导致我们上面的绑定适配器方法`setText(TextView view, CharSequence text)`再次被调用，这也是为什么我们要在`setText(TextView view, CharSequence text)`中做判断避免死循环的原因。
```
@InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
public static String getTextString(TextView view) {
    String s = view.getText().toString();
    Log.d(TAG, "getTextString: s = " + s);
    return s;
}
```
那么这两个方法到底是在那里被调用的呢？这里面涉及到数据绑定框架底层实现原理，暂时没时间研究，只是粗略的说一下。

在这个例子中，数据绑定框架为我们生成了一个类`ActivityBlogDemoBindingImpl`。我们看看其中的一些方法。
```
@Override
protected void executeBindings() {
    //...
    androidx.databinding.ObservableField<java.lang.String> viewModelName = null;
    java.lang.String viewModelNameGet = null;
    MyDataViewModel viewModel = mViewModel;


    //...
    viewModelName = viewModel.getName();
    //注释1处，获取name值
    viewModelNameGet = viewModelName.get();
    //注释2处
    TextViewBindingAdapter.setTextWatcher(this.etText, null, null, null,
             etTextandroidTextAttrChanged);
    //注释3处
    TextViewBindingAdapter.setText(this.etText, viewModelNameGet);
}
```
在执行绑定的时候，会调用executeBindings方法。
在注释1处，获取name的值。
注释2处给EditText添加text改变监听，我们待会再看。
注释3处，调用绑定适配器中的`setText(TextView view, CharSequence text)`方法给EditText设置text。当给EditText设置text以后，会回调到`etTextandroidTextAttrChanged`这个对象的onChange方法中。
```
//注释2处
TextViewBindingAdapter.setTextWatcher(this.etText, null, null, null,
             etTextandroidTextAttrChanged);
```
`etTextandroidTextAttrChanged`是一个InverseBindingListener类型的对象。
```
private androidx.databinding.InverseBindingListener etTextandroidTextAttrChanged = new androidx.databinding.InverseBindingListener() {
    @Override
    public void onChange() {
        //注释1处，从EditText获取text值
        java.lang.String callbackArg_0 = androidx.databinding.adapters.TextViewBindingAdapter.getTextString(etText);
        // localize variables for thread safety
        // viewModel.name.get()
        java.lang.String viewModelNameGet = null;
        // viewModel.name != null
        boolean viewModelNameJavaLangObjectNull = false;
        // viewModel.name
        androidx.databinding.ObservableField<java.lang.String> viewModelName = null;
        // viewModel
        com.example.android.databinding.twowaysample.data.MyDataViewModel viewModel = mViewModel;
        // viewModel != null
        boolean viewModelJavaLangObjectNull = false;

        viewModelJavaLangObjectNull = (viewModel) != (null);
        if (viewModelJavaLangObjectNull) {
            //注释2处，获取name
            viewModelName = viewModel.getName();
            viewModelNameJavaLangObjectNull = (viewModelName) != (null);
            if (viewModelNameJavaLangObjectNull) {
                //注释3处
                viewModelName.set(((java.lang.String) (callbackArg_0)));
            }
        }
    }
};

```

注释1处，调用getTextString方法从EditText获取text值。这个方法就是我们使用@InverseBindingAdapter注解的方法。
注释2处，获取ViewModel中的name。
注释3处，为name重新赋值。

**注意：这里为name重新赋值以后，绑定适配器中的`setText(TextView view, CharSequence text)`方法又会被调用，所以绑定适配器中的`setText(TextView view, CharSequence text)`方法要进行判断，只有新的text和oldText不一致的时候才调用EditText的setText方法。否则会发生死循环。**

### 自定义属性使用双向绑定
自定义属性使用双向绑定，需要使用 `@InverseBindingAdapter`和`@InverseBindingMethod`注解。

例如，如果你想在一个自定义控件`MyView`中为一个自定义`time`属性启用双向绑定，请完成以下步骤。

1. 使用`@BindingAdapter`注解设置和更新`time`属性的方法。
```
@BindingAdapter("time")
@JvmStatic fun setTime(view: MyView, newValue: String) {
    // Important to break potential infinite loops.
    if (view.time != newValue) {
        view.time = newValue
    }
}
```
**在这里我们更新值的时候，一定要注意判断，不要出现死循环。**

2. 使用`@InverseBindingAdapter`注解从`MyView`中获取自定义`time`属性值的方法。
```
@InverseBindingAdapter(attribute = "time", event = "timeAttrChanged")
@JvmStatic fun getTime(view: MyView) : Strings {
    return view.getTime()
}
```
InverseBindingAdapter和一个方法关联，该方法是用来从View中获取属性值的。如果不声明`event`类型类型的话，默认的`event`类型是属性名加上`AttrChanged`后缀。

现在，数据绑定框架知道
1. 当数据发生变化的时候调用`@BindingAdapter("time")`注解的方法。
2. 当view的属性值(可以理解为UI)发生了改变的时候就调用 [`InverseBindingListener`](https://developer.android.com/reference/androidx/databinding/InverseBindingListener)的`onChange`方法，内部会调用`@InverseBindingAdapter("time")`注解的方法来获取最新的属性值。

**但是数据绑定框架还不知道属性何时会发生变化以及属性如何变化。**

为了让数据绑定框架知道，你需要为view设置一个监听器。这个监听器可以是一个自定义的监听器，或者是一个通用的事件，例如失去或者获得焦点、text发生改变等等。使用`@BindingAdapter`注解设置监听器的方法。
```
@BindingAdapter("app:timeAttrChanged")
@JvmStatic fun setListeners(
        view: MyView,
        attrChange: InverseBindingListener
) {
    // Set a listener for click, focus, touch, text change etc.
}
```
设置监听器的方法中传入了一个InverseBindingListener类型的参数。是为了使用InverseBindingListener来告诉数据绑定系统View的属性发生了变化。View的属性发生变化时，数据绑定系统会调用@InverseBindingAdapter注解的方法。

最后，在布局文件里面，使用双向绑定即可。

```
<com.example.android.databinding.twowaysample.MyView
            android:id="@+id/myView"
            time="@={viewModel.time}"
       />
```

### 转换器




参考链接：
1. [Two-way data binding](https://developer.android.com/topic/libraries/data-binding/two-way)




