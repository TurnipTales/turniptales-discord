FROM openjdk:17

WORKDIR /app

COPY discord.jar /app/discord.jar

CMD ["java", "-jar", "discord.jar"]