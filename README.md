# MultiStack ScreenFlow

this is an extension of [ScreenFlow](https://github.com/willhaben/WhScreenFlow) architecture, which is used in our [Android App](https://play.google.com/store/apps/details?id=at.willhaben)



### Features

- support one to five stacks
- build in backstack logic for back press or tab clear (double click on tab)--inspired by Instagram concept
- usecase model managment(inspired by ViewModels from Google ARC)
- deeplink support

In the App you find examples how we combine usecase models, UI states with coroutines channels to decouple UI from asynchronous loading, usecasemodel can handle screen rotation fully and can even be restored from the insidious [process kill](https://medium.com/inloopx/android-process-kill-and-the-big-implications-for-your-app-1ecbed4921cb).
- check **MainActivity** to see how to use the library
- check **DeepLinkActivity** to see how deeplinks are implemented
- check **AzaUseCaseModel** or **LikeAdDetailUseCaseModel** to see how coroutine channels are used