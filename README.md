# Voyager Navigation

This is a Kotlin Multiplatform project targeting Android and iOS where we will showcase the Voyager as the app
navigation.

- Application should allow us to navigate from one screen to another.
- Application should allow to pass some parameters from first to second screen.
- Application should handle the screen rotation without loosing data.
- Application should handle the Tab Navigation.
- Application should handle the async operations with coroutines.

In the next posts I will also cover the [Decompose](https://github.com/mkonkel/DecomposeNavigation), Apyx and
Composer navigation libraries.

### The project:

Base project setup as always is made with [Kotlin Multiplatform Wizard](https://kmp.jetbrains.com), we also need to add
some [Voyager](https://voyager.adriel.cafe) as it is the core
thing that we would like to examine.

```kotlin
[versions]
voyager = "1.1.0-alpha04"

[libraries]
voyager - navigator = { module = "cafe.adriel.voyager:voyager-navigator", version.ref = "voyager" }
voyager - screenmodel = { module = "cafe.adriel.voyager:voyager-screenmodel", version.ref = "voyager" }
voyager - transitions = { module = "cafe.adriel.voyager:voyager-transitions", version.ref = "voyager" }
```

```kotlin
        commonMain.dependencies {
    ...

    implementation(libs.voyager.navigator)
    implementation(libs.voyager.screenmodel)
    implementation(libs.voyager.transitions)
}
```

## Screens

The basic structure for the Voyager is [Screen](https://voyager.adriel.cafe/navigation#screen) interface. Every screen
in our application is just a class with ***@Composable*** function responsible for providing content.
Every screen related class should implement the interface. With quick examination we can see that the ***Screen***
interface is a simple contract with only one method to implement. We can us `object` or `class` for screens without
input parameters, or `data class` in case we need to pass some entry params.

Let's add the screens with basic UI to the project

```kotlin
class FirstScreen : Screen {

    @Composable
    override fun Content() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("First screen")
            Button(onClick = { /*TODO*/ }) {
                Text("Second Screen")
            }
        }
    }
}
```

```kotlin
data class SecondScreen(val greetings: String) : Screen {

    @Composable
    override fun Content() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("First screen")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Greetings: $greetings}")
            Button(onClick = { /*TODO*/ }) {
                Text("Go Back")
            }
        }
    }
}
```

### Navigation

Second most important thing in the Voyager is the [Navigator](https://voyager.adriel.cafe/navigation#navigator) a
compose function build upon the compose internals. The navigator manages ***lifecycle***, ***backPress***,
*StateRestoration*** and the ***navigation*** itself.
To obtain navigator in any `Screen` we want, we should use the ***LocalNavigator*** in the local composition.
As all things where we use in the [CompositionLocal](https://developer.android.com/develop/ui/compose/compositionlocal)
it has to be provided in the root of the composition - the `App()` function.

Let's obtain the ***navigator*** and ***push*** the ***SecondScreen*** from the ***FirstScreen*** - as we can see we
need to instantiate the second screen in the function call.
A cool thing to notice is that the `push` is also a ***infix*** function, so we can use it in a more readable way.

```kotlin
@Composable
private fun FirstScreenButton() {
    val navigator = LocalNavigator.currentOrThrow

    Button(
        onClick = {
            navigator.push(SecondScreen("Hello from First Screen"))
            // infix call:
            // navigator push SecondScreen("Hello from First Screen")
        }
    ) {
        Text("Second Screen")
    }
}
```

We can do same with the ***SecondScreen*** to navigate back to the ***FirstScreen*** with the ***pop*** function.

```kotlin
@Composable
private fun SecondScreenButton() {
    val navigator = LocalNavigator.currentOrThrow

    Button(
        onClick = {
            navigator.pop()
        }
    ) {
        Text("Go Back")
    }
}
```

The last thing to do is to take care of the navigator creation to avoid null-pointers in the composition.
The flexible `Voyager`s approach allow us to choose between various default
screen [transitions](https://voyager.adriel.cafe/transitions), or even create our own. For the sake of this post we will
use the `SlideTransition()`.

```kotlin
@Composable
@Preview
fun App() {
    MaterialTheme {
        Navigator(FirstScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}
```

The initial configuration is up and running, we can navigate between the screens and the data is preserved during the
screen rotation.
Let's run the project on both ***Android*** and ***iOS*** to see the results.

![Navigation](/blog/images/1_navigation.gif "Navigation")

Comparing to the [Decompose](https://github.com/mkonkel/DecomposeNavigation) Voyager also have its own ViewModel
equivalent, called the [ScreenModel](https://voyager.adriel.cafe/screenmodel) but with some lates changes in compose
multiplatform the ViewModels
were [moved to the common code](https://android-review.googlesource.com/c/platform/frameworks/support/+/2965063).
In such situation you are flexible with the approach and can use the one that fits you the best. For the sake of this
post i will use `ScreenModel` from Voyager lib.
The ***ScreenModel*** is designed to store and manage UI-related data with lifecycle awareness and survives
configuration changes. Unlike the ViewModel, the ScreenModel is just an interface. You can create a ***ScreenModel***
only from the [Screen](https://voyager.adriel.cafe/navigation) component.

```kotlin
class FirstScreenModel : ScreenModel {
    val screenTitle = "First screen"
    val buttonText = "Second Screen"
    val greetings = "Hello from First Screen"
}
```

```kotlin
class FirstScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { FirstScreenModel() }
        ...
        Text(screenModel.screenTitle)
    }
}
```

Now if we would have a text input and want to store the value we should use ***state*** inside the ***ScreenModel***. So
it can be recreated after screen rotation.

### Tab Navigation

To use the Tab navigation we need to add another library to our project ***voyager-tab-navigator***.

```kotlin
[libraries]
voyager - tabs = { module = "cafe.adriel.voyager:voyager-tab-navigator", version.ref = "voyager" }
```

```kotlin
commonMain.dependencies {
    ...
    implementation(libs.voyager.tabs)
}
```

When we want to create ***Tab*** we need to usr thr `Tab` interface instead of previously seen `Screen` interface. The
idea behind tabs is the same.
Nevertheless, `TabNavigator` don't support the ***backPress*** and ***Stack API***.
With the ***Tab*** interface we need to implement 2 methods:

- `Content()` - the composable content of the tab
- `options()` - the informations about current tab ***icon***, ***title*** and ***index***.

Let's create the ***FirstTab*** and implement all the methods, with some default values.

```kotlin
object FirstTab : Tab {
    @Composable
    override fun Content() {
        Column {
            Text("First Tab")
        }
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = remember { "First" }
            val icon = rememberVectorPainter(Icons.Default.Home)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }
}
```

The **SecondTab*** should look exactly the same but with different content.

```kotlin
object SecondTab : Tab {
    @Composable
    override fun Content() {
        Column {
            Text("Second Tab")
        }
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = remember { "Second" }
            val icon = rememberVectorPainter(Icons.Default.AccountBox)

            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }
}
```

The last thing to do is to create a container for the tabs, we can follow the steps from the beginning of the post and
create ***TabScreen***. Inside the screen we will use a ***Scaffold*** function with `bottomBar` with component
called `BottomNavigation` from
material lib. The whole content should be wrapped with the `TabNavigator` function.

```kotlin
class TabScreen : Screen {
    @Composable
    override fun Content() {
        TabNavigator(FirstTab) {
            Scaffold(
                bottomBar = {
                    BottomNavigation {

                    }
                }
            ) {

            }
        }
    }
}
```

We can create the helper function `TabNavigationItem()` that will be using the `LocalTabNavigator`  to navigate between
the tabs and ***BottomNavigationItem*** for creating the items.

```kotlin
    @Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    BottomNavigationItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        icon = {
            tab.options.icon?.let {
                Icon(painter = it, contentDescription = tab.options.title)
            }
        }
    )
}
```

Now we can use the helper function.

```kotlin
BottomNavigation {
    TabNavigationItem(FirstTab)
    TabNavigationItem(SecondTab)
}
```

The content of our Scaffold function should display the current tab, to do so we need to just use ***Voyagers***
function `CurrentTab()`

```kotlin
import java.util.Currency

@Composable
override fun Content() {
    Scaffold(
        bottomBar = {
            ...
        }
    ) {
        CurrentTab()
    }
}
```

For the convenience we should add the entrypoint to any existing screen.

```kotlin
@Composable
private fun TabScreenButton() {
    val navigator = LocalNavigator.currentOrThrow

    Button(onClick = { navigator.push(TabScreen()) }) {
        Text("Tabs")
    }
}
```

![Tab Navigation](/blog/images/2_tab_navigation.gif "Tab Navigation")

### Coroutines

The ***ScreenModel*** provides a simple way to handle the async operations with coroutines. Following
the [documentation](https://voyager.adriel.cafe/screenmodel/coroutines-integration) we can implement a countdown timer
toc heck how it works.
The ***Screen*** provides a `screenModelScope` it is cancelled automatically when the screen is disposed.

```kotlin
class FirstScreenModel : ScreenModel {
    ...
    val countDownText = mutableStateOf<String>("0")

    init {
        screenModelScope.launch {
            for (i in 10 downTo 0) {
                countDownText.value = i.toString()
                delay(1000)
            }
        }
    }
}
```

![Coroutines](/blog/images/3_coroutines.gif "Coroutines")

## Summary

The `Voyager` is a great library for the navigation in the Compose Multiplatform projects. It is easy to use and
provides various ways to navigate between the screens. The library is tightly coupled with te `Jetpack Compose` and can
use `ScreenModel` or a `ViewModel` for handling the business logic that's really flexible and can speed up the process
if yu used such approach in the past.
Comparing to the [Decompose](https://github.com/mkonkel/DecomposeNavigation) I have mentioned in previous post it is
simpler to configure and use out of the box. It requires less effort to start with, but in the other hand it is more
tied to Compose itself. Other library for compose multiplatform navigation
is [Appyx](https://github.com/mkonkel/AppyxNavigation) that has lot of similarities to the Voyager. It also is closely
connected to compose, and provide a lot of flexible faetures that can be used in the application. But i find myself more
comfortable with the Voyager, it is more intuitive.

If you are looking fora navigation lib for your compose multiplatform project you definitely should give it a try!