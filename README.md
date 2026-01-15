# registry-ui

### Visualizing and managing private image repositories

This application provides a web user interface which presents the contents of
a Docker private registry. Docker Hub provides a web UI, but its private
registry offering (not to be confused with private repositories) only provides
a REST API.

This initial release does very little: it accumulates the repositories and
the tags within each repository. The reason the word "accumulates" is used
is due to the registry's REST API automatic pagination. Even if the "?n=5"
(using 5 as an arbitrary example) pagination filter is not explicitly used,
the registry service can return partial results in the event of a lengthy
response. Since the results are accumulated, we are able to determine the
number of results and offer true pagination (not currently implemented). The
API only offers one-way pagination since the results resemble a singly linked
list as opposed to a doubly linked list. In order to be able to return to a
previous set of results, we must maintain that accumulated result set.
While internal data structures can be used to maintain a cache of the results,
this is not ideal in a distributed environment where multiple instances of
the application may be running (e.g. Kubernetes replicas). For this reason,
Redis is used as a caching mechanism which can be shared by multiple instances.

In addition to displaying tags for repositories, the UI provides the
ability to delete one or more tags for a given repository.


## Design Decisions

registry-ui uses a balance of server rendering pages with dynamic content
via JavaScript. After performing several performance tests, this approach
became the preferred one. Additionally, maintaining the Thymeleaf template
became much cleaner than the pure JavaScript approach.

At present, registry-ui can display all repositories and their tags, and
tags can also be deleted (multiple simultaneously allowed). Clicking on a
tag will display the layers for that tag, and each line can be clicked on
to download that layer. The download portion has not been implemented, but
it will leverage streaming rather than filling up an entire buffer and
serving the contents.

!["registry-ui](images/registry-ui.png)


## What's next?

"What's first" should have been the unit tests. Since this is a personal
project, I dispensed with the formalities. However, future development will
follow the TDD approach.

Authentication and authorization will be added, which will depend on a
database. JPA will be used rather than low-level JDBC.


## Q & A
**Why didn't you use jQuery?**  
An entire library which occasionally has security flaws just for AJAX?

**How about Angular or React for the front end?**  
The only front end framework I have used at length is ExtJS, which is
quite comprehensive. If there is still a community edition, that may be
something of interest. I have held off on Bootstrap for this reason.
Quite frankly, I am not a fan of TypeScript, so I will not even entertain
the idea of Angular or React.

**Why didn't you do this in python using flask, Django, or similar?**  
One of python's strengths is its ecosystem and the ease to learn the
language, but the core language seems to be stagnating. Plus, dependencies
for one platform may be different from another platform (requirements.txt).
Another benefit of Java is that the JSON response can be unserialized
directly into objects, which validates the structure of the response. If
unmarshalling into plain maps and lists, the structure of the response needs
to be validated. The last note: a Java/Spring-Boot based solution is  much more portable. I have tested this on an M1 MacBook Pro, an IdeaPad
laptop, an x86-64 server (AMD), and a Raspberry Pi4 running a 64-bit
version of Ubuntu Server.

## Running the app

```shell
java -Dspring.profiles.active=local -jar build/libs/registry-ui-1.0.0.jar
```