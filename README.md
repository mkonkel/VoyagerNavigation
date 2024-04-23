# Voyager Navigation

This is a Kotlin Multiplatform project targeting Android and iOS where we will showcase the Voyager as the app
navigation.

- Application should allow us to navigate from one screen to another.
- Application should allow to pass some parameters from first to second screen.
- Application should handle the screen rotation without loosing data.
  ...

In the next posts I will also cover the [Decompose](https://github.com/mkonkel/DecomposeNavigation), Circuit, Apyx and
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
The flexible `Voyager`s approach allow us to choose between various default screen [transitions](https://voyager.adriel.cafe/transitions), or even create our own. For the sake of this post we will use the `SlideTransition()`.

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

The initial configuration is up and running, we can navigate between the screens and the data is preserved during the screen rotation.
Let's run the project on both ***Android*** and ***iOS*** to see the results.





---

Comparing to the [Decompose](https://github.com/mkonkel/DecomposeNavigation) Voyager also have its own ViewModel
equivalent, called the [ScreenModel](https://voyager.adriel.cafe/screenmodel) but with some lates changes in compose
multiplatform the ViewModels
were [moved to the common code](https://android-review.googlesource.com/c/platform/frameworks/support/+/2965063).
In such situation you are flexible with the approach and can use the one that fits you the best. For the sake of this
post i will use `ScreenModel` from Voyager lib.
The ***ScreenModel*** is designed to store and manage UI-related data with lifecycle awareness and survives
configuration changes. Unlike the ViewModel, the ScreenModel is just an interface. You can create a ***ScreenModel***
only from the [Screen](https://voyager.adriel.cafe/navigation) component.

TODO: Add the ScreenModel configuration here with basic business logic for resenting text, button clicks.

### Summary