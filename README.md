# MediaWiki Tests

Api tests for MediaWiki functionality using Rest Assured

## Project Dependencies
- Java 11
- Maven
- Lombok plugin for IDE and annotations enabled

## Approach
- My first step in understanding the task was to read through the documentation before creating any automated checks
- I found the documentation quite difficult to navigate through but needless to say this is the fun part of testing applications and software
- I studied the documentation and explored the application to answer a number of questions to help shape my approach. See below

### Findings
I explored the following areas
    - Can I recreate the journeys that need to be tested?
        - I did this using the documentation available, using the Sandbox feature to create requests and by creating my own postman collection. See [mediawiki.postman_collection.json](mediawiki.postman_collection.json)
        - I used a combination of the documentation and reference code to help me recreate the requests.
        - Whilst one can argue that the response is enough to validate, I like to go beyond this to validate CRUD operations. I did this especially with create and edit pages by getting the revision data created.
    - What are the dependencies for each journey?
        - In order to generate valid tokens I needed an account which I created to be able to explore the functionality better.
        - Once I created an account, I recreated this request in postman and was able to obtain valid tokens with a valid session.
    - What are the validation rules for the apis?
        - The documentation is vast and not the best laid out, so I focused on understanding the rules for the journeys I was testing.
        - Tokens are integral to be able to perform a lot of the functionality.
    - Where can I test?
        - I decided to stick with testing the functionality against test.wikipedia.com given that this is powered entirely by mediawiki and was the safest

## Automated Checks
- After understanding the journey flows I decided to use a combination of rest assured and junit to create lightweight integration/journey tests.
- Given that these tests focus on the APIs, anything more than this felt like an overkill. The combination of junit as the runner and rest assured seemed like a good approach given the time available, and allowed me to create tests pretty quickly
- My initial approach was to get tests working for the journeys being tested, so at a high level I abstracted out concepts such as login, token generation, create and edit.
- Once I had the tests working, I applied DRY and OOP concepts to make the code cleaner
- I've separated out the requests in their own classes to return the responses and to assert them in the tests. 
- Some of the classes do also include functions such as getTokenByName. See project structure below:

```
├── src
│   └── test
│       ├── java
│       │   ├── CreatePageTests.java
│       │   ├── EditPageTests.java
│       │   ├── LoginTests.java
│       │   ├── TokenGenerationTests.java
│       │   ├── requests
│       │   │   ├── BaseRequestSpecification.java
│       │   │   ├── EditPageRequests.java
│       │   │   ├── GetPageRevisionsRequest.java
│       │   │   ├── GetTokenRequests.java
│       │   │   ├── LoginRequests.java
│       │   │   └── LogoutRequests.java
│       │   └── utils
│       │       └── Helper.java
│       └── resources

```

- I've implemented setUp and tearDown steps
- I've included some negative, parameterised tests around valid. Given time, I would have liked to expand these further after understanding the functionality better

## Improvements
- DTOS
- POJOS
- ENV VARS
- Add github workflows
