# kaocha-doctest

<!-- badges -->
[![cljdoc badge](https://cljdoc.org/badge/com.lambdaisland/kaocha-doctest)](https://cljdoc.org/d/com.lambdaisland/kaocha-doctest) [![Clojars Project](https://img.shields.io/clojars/v/com.lambdaisland/kaocha-doctest.svg)](https://clojars.org/com.lambdaisland/kaocha-doctest)
<!-- /badges -->

Doctest test type for Kaocha

Hot off the press, this is proof of concept so far. Caveat programmator!

## Features

<!-- installation -->
## Installation

To use the latest release, add the following to your `deps.edn` ([Clojure CLI](https://clojure.org/guides/deps_and_cli))

```
com.lambdaisland/kaocha-doctest {:mvn/version "0.1.15"}
```

or add the following to your `project.clj` ([Leiningen](https://leiningen.org/))

```
[com.lambdaisland/kaocha-doctest "0.1.15"]
```
<!-- /installation -->

## Rationale

It can be very helpful to put some examples in a docstring of how to use a
function, with this test type these now become tests as well, so you make sure
they work, and continue to work.

This project introduces a new kaocha "test type", you define a separate suite of
type doctest, and it'll scan the given directories for these tests inside
docstrings.

## Usage

Write doctests:

```clj
(defn sum
  "This function computes a sum

  (sum 1 2 3)
  ;; => 6
  (sum 4 5 6)
  ;; => 10
  "
  [& args]
  (apply + args))
```

Define a doctest test suite:

```clj
;; tests.edn
#kaocha/v1
{:tests   [{:id          :doctests
            :type        :kaocha.type/doctest
            ;; Currently these are needed, or it will only load `test/.*_test.clj`, we're working on fixing that.
            :test-paths  ["src"]
            :ns-patterns [".*"]}]
 :reporter [kaocha.report/documentation]}
```

Run `bin/kaocha` (see the Kaocha docs for more info on setting up and running Kaocha).

## Limitations

This is currently (and may remain) Clojure-only.

<!-- opencollective -->
## Lambda Island Open Source

[Become a backer](https://opencollective.com/lambda-island) and show your support to our open source projects.

<a href="https://opencollective.com/lambda-island">
<img src="https://opencollective.com/lambda-island/tiers/enterprise-sponsor.svg?limit=30&button=false&avatarHeight=46">
<img src="https://opencollective.com/lambda-island/tiers/startup-sponsor.svg?limit=30&button=false&avatarHeight=46">
<img src="https://opencollective.com/lambda-island/tiers/bootstrapper.svg?limit=30&button=false&avatarHeight=46">
<img src="https://opencollective.com/lambda-island/tiers/hobbyist.svg?limit=30&button=false&avatarHeight=46">
</a>
<img align="left" src="https://github.com/lambdaisland/open-source/raw/master/artwork/lighthouse_readme.png">

&nbsp;

kaocha-doctest is part of a growing collection of quality Clojure libraries created and maintained
by the fine folks at [Gaiwan](https://gaiwan.co).

Pay it forward by [becoming a backer on our Open Collective](http://opencollective.com/lambda-island),
so that we may continue to enjoy a thriving Clojure ecosystem.

You can find an overview of our projects at [lambdaisland/open-source](https://github.com/lambdaisland/open-source).

&nbsp;

&nbsp;
<!-- /opencollective -->

<!-- contributing -->
## Contributing

We warmly welcome patches to kaocha-doctest. Please keep in mind the following:

- adhere to the [LambdaIsland Clojure Style Guide](https://nextjournal.com/lambdaisland/clojure-style-guide)
- write patches that solve a problem 
- start by stating the problem, then supply a minimal solution `*`
- by contributing you agree to license your contributions as MPL 2.0
- don't break the contract with downstream consumers `**`
- don't break the tests

We would very much appreciate it if you also

- update the CHANGELOG and README
- add tests for new functionality

We recommend opening an issue first, before opening a pull request. That way we
can make sure we agree what the problem is, and discuss how best to solve it.
This is especially true if you add new dependencies, or significantly increase
the API surface. In cases like these we need to decide if these changes are in
line with the project's goals.

`*` This goes for features too, a feature needs to solve a problem. State the problem it solves first, only then move on to solving it.

`**` Projects that have a version that starts with `0.` may still see breaking changes, although we also consider the level of community adoption. The more widespread a project is, the less likely we're willing to introduce breakage. See [LambdaIsland-flavored Versioning](https://github.com/lambdaisland/open-source#lambdaisland-flavored-versioning) for more info.
<!-- /contributing -->

<!-- license -->
## License

Copyright &copy; 2023 Arne Brasseur and Contributors

Licensed under the term of the Mozilla Public License 2.0, see LICENSE.
<!-- /license -->
