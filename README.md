# Notable

<img src="https://user-images.githubusercontent.com/45648517/218002812-154a6b51-c36d-4eba-8779-ab3129ca6b72.png"  width="300" height="300">

[![Pull Request Workflow](https://github.com/Elijah-Dangerfield/Notable/actions/workflows/on_pr.yml/badge.svg?branch=main)](https://github.com/Elijah-Dangerfield/Notable/actions/workflows/on_pr.yml)

The goal of this project is to help me understand the basics of Compose and continue to grow my Android development skills. As I build this app, I aim to improve my skills and knowledge of the platform and develop a functional app.

### Project Mentionables: 
- detekt and checkstyle for static code analysis
- Github actions for CI/CD
- convention plugins and version catalogs for ease of modularization

# Screenshots
![](https://user-images.githubusercontent.com/45648517/218001548-270e9fff-c00a-44b7-a15d-80f2683e7ab2.png)

# How to run

To just play with the app you can find apks and aabs attached to the latest [release](https://github.com/Elijah-Dangerfield/Notable/releases)

For building locally, I like to keep imporant files off the internet. Some of those files are needed to run this project, so by default running this project will fail. If you'd like to run this project feel free to contact me and I will send you those secret files :) 

Contact info : elijahdangerfield111@gmail.com



# Architecture

This app was built with the latest technologies and guided by the principles outlined in the Guide to App Architecture. Specifically, this app utilizes unidirectional data flow, ensuring a smooth and efficient user experience.

# Modularization

The modularization followed in this code base aims to encourage low coupling and high cohesion. 

It is separated into `core` and `feature` modules. The `app` module leverages these to glue together the app experience. 

Rules:
- a feature module should never depend on another feature module. Communication across modules can be defined in an interface and handled through the app module. 
- A feature module may depend on core modules
- a core module may not depend on feature modules. 
- when applicable core modules should be split into api and impl modules and glued together in the app module. This enforces abstraction and hides implementation details. 
- core modules can depend on the api module of another core module. 

Additionally I leverage a `build-logic` included build with convention plugins to cut down on build time and make the build process easier to understand and update

# CI/CD

This project includes a basic yet opinionated CI/CD system leveraging Github Actions.
The workflows for these can be found under `.github/workflows`
To read more about the CI/CD of this project visit the [documentation](https://github.com/Elijah-Dangerfield/notable/blob/main/docs/ci.md)
