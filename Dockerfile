FROM openjdk:17

WORKDIR /app

COPY turniptales-discord.jar /app/turniptales-discord.jar

CMD ["java", "-jar", "turniptales-discord.jar"]