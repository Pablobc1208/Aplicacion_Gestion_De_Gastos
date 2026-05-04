# StudentPocket - Gestión de Gastos Estudiantil

## Resumen del Proyecto
Este proyecto es una solución integral para el control de finanzas personales, denominada **StudentPocket**, desarrollada en Java como parte de la Actividad 8 de Programación. La aplicación permite a los usuarios registrar ingresos y gastos, categorizarlos y visualizar su balance financiero en tiempo real. 

El sistema utiliza una arquitectura **MVC (Modelo-Vista-Controlador)** y patrones **DAO (Data Access Object)** para separar la lógica de negocio de la persistencia de datos.

### Características Principales
- **Sistema Multi-rol**: 
  - **Administrador**: Acceso total a las transacciones de todos los usuarios.
  - **Estudiante**: Gestión exclusiva de sus propias transacciones personales.
- **Visualización de Datos**: Integración de gráficos circulares (Pie Charts) para el análisis de gastos por categoría.
- **Filtros Avanzados**: Buscador multicapa por categoría, nombre de usuario y rango de fechas.
- **Diseño Moderno**: Interfaz de usuario premium utilizando el Look and Feel FlatLaf.
- **Persistencia Robusta**: Base de datos SQLite local para garantizar la portabilidad sin necesidad de configurar servidores externos.
- **Importación Inteligente**: Al iniciar por primera vez, el sistema importa automáticamente datos desde un archivo CSV para facilitar las pruebas.

---

## Requisitos y Dependencias

### Tecnologías Core
- **Lenguaje**: Java 17 (o superior)
- **Gestor de Proyectos**: Apache Maven 3.6+
- **Base de Datos**: SQLite 3

### Librerías Externas (Dependencies)
El proyecto utiliza las siguientes librerías gestionadas a través del `pom.xml`:

1.  **FlatLaf (v3.4.1)**: Utilizada para modernizar la interfaz gráfica de Swing, proporcionando un aspecto limpio y profesional similar a las aplicaciones modernas.
2.  **JFreeChart (v1.5.3)**: Librería especializada para la generación de gráficos estadísticos (Gráfico de tarta de gastos).
3.  **SQLite JDBC (v3.42.0.0)**: Driver necesario para la comunicación entre Java y el motor de base de datos SQLite.

---

## Instalación y Ejecución

1.  **Clonación/Descarga**: Extraer el contenido del proyecto.
2.  **Preparación**: Asegurarse de que el puerto de red no bloquee la descarga de dependencias de Maven.
3.  **Compilación**: Ejecutar el comando:
    ```bash
    mvn clean install
    ```
4.  **Ejecución**: Iniciar la aplicación ejecutando la clase principal:
    `com.gastos.main.Main`

---

## Estructura de Paquetes
- `com.gastos.main`: Punto de entrada y configuración global del Look and Feel.
- `com.gastos.model`: Clases de entidad (`Usuario`, `Transaccion`).
- `com.gastos.dao`: Lógica de acceso a datos (`DatabaseConfig`, `UsuarioDAO`, `TransaccionDAO`).
- `com.gastos.view`: Componentes de la interfaz gráfica (`LoginFrame`, `DashboardFrame`).
- `resources`: Recursos estáticos (Logo, CSS ficticio, CSV de datos iniciales).

---
