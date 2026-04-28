# Aplicación de Gestión de Gastos Estudiantil

Esta aplicación permite a los estudiantes gestionar sus ingresos y gastos de forma sencilla, permitiendo además al administrador supervisar todas las transacciones.

## Requisitos
- Java 17 o superior.
- Maven.

## Características
- **Base de Datos**: SQLite (Persistence local).
- **Importación**: Carga automática de datos iniciales desde CSV.
- **Seguridad**: Sistema de Login con roles (Administrador/Estudiante).
- **Visualización**: Gráficos interactivos de gastos por categoría (JFreeChart).
- **Filtros**: Búsqueda avanzada por categoría, usuario y rango de fechas.

## Cómo Ejecutar
1. Clonar el repositorio.
2. Ejecutar `mvn clean install` para descargar dependencias.
3. Ejecutar la clase `com.gastos.main.Main`.

## Estructura del Proyecto
- `com.gastos.model`: Clases de entidad (POJO).
- `com.gastos.dao`: Acceso a datos y configuración de SQLite.
- `com.gastos.view`: Interfaz gráfica Swing (mejorada con FlatLaf).
- `com.gastos.main`: Punto de entrada de la aplicación.