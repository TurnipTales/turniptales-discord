FROM openjdk:24

WORKDIR /app

COPY turniptales-discord.jar /app/turniptales-discord.jar

CMD ["java", "-jar", "turniptales-discord.jar"]