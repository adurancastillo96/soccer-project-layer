# ⚽ Soccer Manager (Layered Architecture Project)
![Build Status](https://github.com/adurancastillo96/soccer-project-layer/actions/workflows/build.yml/badge.svg?branch=feature/gradle-migration)

Este proyecto es una aplicación de gestión de equipos de fútbol desarrollada en **Java**. Demuestra una **Arquitectura en Capas (Layered Architecture)** robusta y moderna, utilizando **Gradle** para la gestión de dependencias y un diseño orientado a eventos (**Event-Driven**) para desacoplar la lógica de negocio.

-----

## 🚀 Características Principales

* **Gestión de Equipos:** Crear, consultar y eliminar equipos con validaciones de negocio.
* **Gestión de Jugadores:** Fichar y despedir jugadores, controlando dorsales únicos y reglas de negocio.
* **Simulador de Partidos:** Motor de simulación probabilístico basado en la plantilla de jugadores.
* **Persistencia Robusta:**
    * **En Memoria:** Uso de colecciones concurrentes (`ConcurrentHashMap`) para alto rendimiento.
    * **En Disco:** Persistencia automática en **JSON** (vía librería Jackson) y **CSV** como respaldo.
* **Sistema de Eventos Asíncrono:** Bus de eventos propio para manejar feedback en consola y guardado en disco sin bloquear el hilo principal.

-----

## 🛠️ Stack Tecnológico

El proyecto ha sido migrado a un entorno de desarrollo profesional:

* **Lenguaje:** Java 17
* **Build Tool:** Gradle (Groovy DSL)
* **Librerías:**
    * **Jackson:** Para procesamiento eficiente y robusto de archivos JSON.
    * **JUnit 5:** Para pruebas unitarias y de integración.
* **CI/CD:** GitHub Actions para integración continua automática.

-----

## 🏗️ Arquitectura del Sistema

El proyecto sigue el estándar de directorios de Gradle (`src/main/java`) y una estricta separación de responsabilidades:

1.  **Capa de Presentación (UI):** (`ui`) - Interacción con el usuario por consola.
2.  **Capa de Aplicación (Service):** (`service`) - Orquestación de flujos y reglas de negocio.
3.  **Capa de Dominio (Model & Events):** (`model`, `events`, `domain`) - Entidades y eventos del núcleo.
4.  **Capa de Infraestructura (Persistence & Repository):** (`repository`, `persistence`) - Almacenamiento de datos.

### 📂 Estructura del Proyecto

```text
src/
├── main/
│   └── java/
│       ├── domain/           # Excepciones y Enums (Reglas)
│       ├── events/           # EventBus y Definición de Eventos
│       ├── model/            # Entidades: Team y Player
│       ├── persistence/      # Serialización Jackson y Listeners
│       ├── repository/       # Interfaces e implementación en memoria
│       ├── service/          # Lógica de negocio
│       ├── ui/               # Menú de consola y controladores
│       ├── util/             # Utilidades
│       └── Main.java         # Punto de entrada
└── test/
    └── java/                 # Tests Unitarios y de Integración
```

-----

## ⚙️ Instalación y Ejecución

No necesitas instalar nada extra, el proyecto incluye el **Gradle Wrapper**.

### 1\. Clonar el repositorio

```bash
git clone https://github.com/adurancastillo96/soccer-project-layer.git
cd soccer-project-layer
```

### 2\. Ejecutar la aplicación

Usa el wrapper de Gradle para compilar y ejecutar el proyecto automáticamente:

* **Linux / Mac:**
  ```bash
  ./gradlew run
  ```
* **Windows:**
  ```cmd
  gradlew run
  ```

### 3\. Ejecutar los Tests

Para verificar que toda la lógica de negocio y la persistencia funcionan correctamente:

```bash
./gradlew test
```

*Esto ejecutará la suite de pruebas con JUnit 5 y generará un reporte de resultados.*

-----

## 💾 Persistencia y Datos

El sistema utiliza una estrategia de persistencia atómica tolerante a fallos:

* **Carga Inicial:** Intenta cargar `teams.json` y `players.json`. Si no existen, hace fallback a los archivos `.csv`.
* **Guardado:** Gracias a **Jackson**, los objetos se serializan a JSON limpio. Se utiliza una escritura en archivo temporal (`.tmp`) seguida de un movimiento atómico para evitar corrupción de datos si el programa se cierra inesperadamente.

-----

## 🧩 Detalles Técnicos Destacados

1.  **Inyección de Dependencias Manual:** En `Main.java`, las dependencias se inyectan manualmente, demostrando el principio de Inversión de Control sin frameworks pesados.
2.  **Event Bus Personalizado:** Implementación propia de un Bus de Eventos asíncrono para notificaciones UI y persistencia en segundo plano.
3.  **Clean Code:** Código refactorizado siguiendo principios SOLID, eliminando "code smells" y utilizando convenciones de nombres consistentes.

-----

## 👥 Autor

Proyecto desarrollado como práctica avanzada de arquitectura de software en Java.