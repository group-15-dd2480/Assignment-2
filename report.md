# Assignment 2 - Group 15 - Report

### Members

- Francis Kloskowski Gniady
- Markus Wessén
- David Aldenbro
- Zeynep Ebrar Karadeniz

---

## Essence Statement

Since this was something most of us didn't know before, we have regressed from fifth stage to fourth stage, in place. We strongly agreed on the practices and the tools we should use. Although the use of practices are supported by the team, whole team didn't use all the tools.

## Workflow

### Work division

We first divided the work to large pieces like Webhook Endpoints, GitHub Status API etc. and created issues for each part.

Then we vaguely divided most of the issues, and then as more issues were added, we assigned ourselves to the issues we were/planed to work on.

### Forks

Each person has their own for of the main repository that they can work in.

### Committing changes

All changes to the main repository are made through pull requests, each pull request gets reviewed and approved by at least one person before merging. Each pull request is linked to an issue that it addresses.

### CI

We have a github actions workflow to run our tests and ensure the code can be compiled on each pull request. We do not merge a pull request if CI does not pass.

### Commit message conventions

We use commit message conventions (`<type>: <message>`) to make it easier to understand what type of commit it is, for example `feat: add X feature` or `fix: fix bug Y`.
