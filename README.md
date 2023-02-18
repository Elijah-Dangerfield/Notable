# Notable

<img src="https://user-images.githubusercontent.com/45648517/218002812-154a6b51-c36d-4eba-8779-ab3129ca6b72.png"  width="300" height="300">

The goal of this project is to help me understand the basics of Compose and continue to grow my Android development skills. As I build this app, I aim to improve my skills and knowledge of the platform and develop a functional app.

### Project Mentionables: 
- detekt and checkstyle for static code analysis
- Github actions for CI/CD
- convention plugins and version catalogs for ease of modularization

# Screenshots
![](https://user-images.githubusercontent.com/45648517/218001548-270e9fff-c00a-44b7-a15d-80f2683e7ab2.png)

# How to run

This app utilizes a firebase backend. 

When building a check will run to ensure that the files needed by firebase are present. If they are not present during this check, a `drive-service-account-key.json` file is utilized to download the needed files from google drive. 
If you would like to run this project I would love to give you access by sending you that json file :) (elijahdangerfield111@gmail.com)

# Architecture

This app was built with the latest technologies and guided by the principles outlined in the Guide to App Architecture. Specifically, this app utilizes unidirectional data flow, ensuring a smooth and efficient user experience.

# Modularization

The modularization followed in this code base aims to encourage low coupling and high cohesion. 

It is separated into `core` and `feature` modules. The `app` module leverages these to glue together the app experience. 

Rules:
- a feature module should never depend on another feature module. It may depend on core modules
- a core module should never depend on another module
- when applicable core modules should be split into api and impl modules and glued together in the app module. This enforces abstraction and hides implementation details. 

Additionally I leverage a `build-logic` included build with convention plugins to cut down on build time and make the build process easier to understand and update

# CI/CD

This project includes a basic yet opinionated CI/CD system leveraging Github Actions.
The workflows for these can be found under `.github/workflows`
To read more about the CI/CD of this project visit the [documentation](https://github.com/Elijah-Dangerfield/notable/blob/main/docs/ci.md)
