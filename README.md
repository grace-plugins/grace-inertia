[![Main branch build status](https://github.com/grace-plugins/grace-inertia/workflows/Grace%20CI/badge.svg?style=flat)](https://github.com/grace-plugins/grace-inertia/actions?query=workflow%3A%Grace+CI%22)
[![Apache 2.0 license](https://img.shields.io/badge/License-APACHE%202.0-green.svg?logo=APACHE&style=flat)](https://opensource.org/licenses/Apache-2.0)
[![Latest version on Maven Central](https://img.shields.io/maven-central/v/org.graceframework.plugins/inertia.svg?label=Maven%20Central&logo=apache-maven&style=flat)](https://search.maven.org/search?q=g:org.graceframework.plugins)
[![Grace Document](https://img.shields.io/badge/Grace_Document-latest-blue?style=flat&logo=asciidoctor&logoColor=E40046&labelColor=ffffff&color=f49b06)](https://plugins.graceframework.org/grace-inertia/latest/)
[![Grace on X](https://img.shields.io/twitter/follow/graceframework?style=social)](https://x.com/graceframework)

[![Groovy Version](https://img.shields.io/badge/Groovy-3.0.22-blue?style=flat&color=4298b8)](https://groovy-lang.org/releasenotes/groovy-3.0.html)
[![Grace Version](https://img.shields.io/badge/Grace-2022.2.8-blue?style=flat&color=f49b06)](https://github.com/graceframework/grace-framework/releases/tag/v2022.2.8)

# Grace with Inertia

Grace Plugin for using Grace/Grails app with [Inertia.js](https://inertiajs.com).

## Ducumentation

* [Latest](https://plugins.graceframework.org/grace-inertia/latest/)

## Usage

Add dependency to the `build.gradle`,

```gradle

repositories {
    mavenCentral()
}

dependencies {
    implementation "org.graceframework.plugins:inertia:VERSION"
}
```

Inertia plugin supports controller-specific `withFormat()` method,

```groovy
class BookController {

    def list() {
        def books = Book.list()

        withFormat {
            inertia {
                render(inertia: "Book/List", props: [bookList: books])
            }
            json {
                render books as JSON
            }
        }
    }
}
```

Also, this plugin supports extendsions for Grails Request and Response,

```groovy
// You can get Inertia request headers from Grails Request

request.inertia.version == request.getHeader('X-Inertia-Version')

// Check Inertia request?
if (request.inertia as boolean) { // or use request.isInertia()
    template = 'book-detail'
}

// You can set Inertia response headers in Grails

response.inertia.location = 'http://localhost:8080/book/1'

```

## Development

### Build from source

```
git clone https://github.com/grace-plugins/grace-inertia.git
cd grace-inertia
./gradlew publishToMavenLocal
```

## Support Version

* Grace 2022.0.0+
* Grails 5.0+

## Roadmap

### 1.x

* Inertia 1.x

## License

This plugin is available as open source under the terms of the [APACHE LICENSE, VERSION 2.0](http://apache.org/Licenses/LICENSE-2.0)

## Links

- [Grace Framework](https://github.com/graceframework/grace-framework)
- [Grace Plugins](https://github.com/grace-plugins)
- [Grace Inertia Plugin](https://github.com/grace-plugins/grace-inertia)
- [Inertia](https://inertiajs.com)
