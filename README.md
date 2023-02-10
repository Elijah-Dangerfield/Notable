# Notable

<img src="https://user-images.githubusercontent.com/45648517/218002812-154a6b51-c36d-4eba-8779-ab3129ca6b72.png"  width="300" height="300">
This project is my dive into the world of Compose. This isn't necessarily reflective of best practices but rather my learning progress.

Notable is a work in progress 🚧 Android app built with Kotlin and Jetpack Compose. The goal of this project is to help me understand the basics of Compose and continue to grow my Android development skills. As I build this app, I aim to improve my skills and knowledge of the platform and develop a functional app that can be used as a reference for other developers.

The app is currently in development, and its features and capabilities will continue to change and evolve as I learn more about Compose and Android development.

# Screenshots
![](https://user-images.githubusercontent.com/45648517/218001548-270e9fff-c00a-44b7-a15d-80f2683e7ab2.png)

# How to run

This app utilizes a firebase backend. When building a check will run to ensure that the files needed by firebase are present. If they are not present during this check, a `drive-service-account-key.json` file is utilized to download the needed files from google drive. 
If you would like to run this project I would love to give you access by sending you that json file :) (elijahdangerfield111@gmail.com)

# Architecture

This app was built with the latest technologies and guided by the principles outlined in the Guide to App Architecture. Specifically, this app utilizes unidirectional data flow, ensuring a smooth and efficient user experience.

# Modularization

The modularization followed in this code base aims to encourage low coupling and high cohesion. It is separated into `core` and `feature` modules. The `app` module leverages these to glue together the app experience. Additionally I leverage a `build-logic` included build with convention plugins to cut down on build time and make the build process easier to understand and update
