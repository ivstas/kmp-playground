## Development workflow

### 1. Assemble ES client module from kotlin/js sources
Either run `:client:jsBrowserDevelopmentLibraryDistribution` or use corresponding run configuration in project.

The development build is used until [this kRPC bug is fixed](https://github.com/Kotlin/kotlinx-rpc/issues/178)

### 2. Build the js bundle
Run one of the following `npm` tasks in **web** directory:
- `build:production` to assemble frontend once.
- or `build:watch` if you want to update bundle when sources change

### 3. Run the ktor server
Server will both
- serve the frontend bundle from static directory
- and serve the kRPC API from the corresponding route

Run `server` configuration in project.
