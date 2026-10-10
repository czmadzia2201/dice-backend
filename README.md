# Dice

## Overview

A full-stack implementation of the Dice game (also known as Yahtzee) built with **Spring Boot** and **Angular**.

Apart from the standard gameplay, the application introduces two unique post-game analysis features: manual category assignment and optimal score calculation. Those features are more closely described below.

Backend and frontend of the application are maintained as separate projects. The frontend repository is available here:

**Frontend:** https://github.com/czmadzia2201/dice-frontend

---

## Motivation to create the game

The project started out of frustration with dice games in which a perfect roll often appears only after the matching category has already been used.
I wanted to give players the opportunity to 'fix' this by adding a post-game category assignment feature.
To complement it, the application also calculates the optimal score, showing how close the player's manual assignment is to the best possible result.
Together, these two features transform the application from a simple dice game into a post-game analysis tool.

The first version of the game was built with Spring Boot and Thymeleaf. It was later rewritten with an Angular frontend as a way to learn Angular and separate the frontend from the backend.

---

## Technologies

### Backend

- Java
- Spring Boot
- Maven
- REST API
- JUnit 5
- Mockito

### Frontend

- Angular
- TypeScript
- HTML / CSS

---

## Gameplay

### Classic game

The application supports both classic game modes:

- **Single Roll** – each turn consists of a single dice roll.
- **Three Rolls** – the player may roll up to three times per turn, choosing which dice to keep between rolls.

### Post-game Category Assignment

Once the game has finished, the player can manually assign completed rolls to score categories without the restrictions imposed during normal gameplay. This allows experimenting with different combinations and comparing alternative scoring strategies.

### Optimum

The application calculates the maximum score that could have been achieved for the completed game by finding the best possible assignment of recorded rolls to score categories. The optimal score is then compared with the player's actual result, showing how much room there was for improvement.

---

## Running the Project Locally

### Backend

```bash
mvn spring-boot:run
```

### Frontend

```bash
npm install
npm start
```

### Swagger
http://localhost:8080/dice/swagger-ui/index.html

---

## Development

This project was built with some assistance from AI tools, primarily ChatGPT, which served as a discussion partner and guide for frontend development.

The overall architecture, design decisions, implementation, code reviews and final solutions were designed, evaluated and refined by the author.

---

## To Do List

- Publish the application online. 
