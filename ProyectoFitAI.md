# Contexto del Proyecto: "FitAI: Arquitectura y Diseño del Entrenador Inteligente de Gimnasio"

## 1. Resumen del Proyecto

* **Nombre del sistema:** FitAI.
* **Naturaleza:** Aplicación móvil orientada al entrenamiento físico.
* **Objetivo principal:** Permitir a los usuarios crear rutinas de ejercicio, registrar entrenamientos en tiempo real, analizar su progreso visualmente y recibir asistencia mediante IA.
* **Usuario objetivo:** Personas que realizan actividad física y desean llevar un control organizado de sus entrenamientos.
* **Contexto de desarrollo:** Proyecto enmarcado en el curso de "Desarrollo de Sistemas Móviles", aplicando buenas prácticas de ingeniería de software, tecnologías emergentes y metodologías ágiles.

---

## 2. Requisitos y Características del Sistema

### Funcionalidades Clave

* Registro e inicio de sesión de usuarios.
* Creación de rutinas personalizadas y generación automática según objetivo (hipertrofia, fuerza, mixto), nivel y frecuencia.
* Edición de rutinas y registro de entrenamientos en tiempo real (repeticiones y peso).
* Almacenamiento del historial de entrenamientos y visualización de progreso mediante gráficos.
* Conteo de pasos mediante sensores del dispositivo y almacenamiento de fotos de progreso.
* Interacción con un chat de asistencia basado en inteligencia artificial y recepción de notificaciones.

### Requisitos No Funcionales

* **Usabilidad:** Interfaz intuitiva basada en Material Design.
* **Disponibilidad:** Acceso a datos en modo offline mediante base de datos SQLite (Room).
* **Seguridad y Escalabilidad:** Autenticación y servicios en la nube gestionados con Firebase.
* **Rendimiento:** Respuesta rápida en operaciones principales.

---

## 3. Arquitectura y Stack Tecnológico

* **Patrón de Arquitectura:** Patrón MVVM (Model-View-ViewModel).
* **Lenguaje:** Kotlin para Android.
* **Servicios Backend y Cloud:** Firebase (Authentication, Firestore, Storage).
* **Base de Datos Local:** SQLite utilizando la biblioteca Room.
* **Consumo de APIs:** Uso de Retrofit para APIs externas (ejercicios e IA).
* **Procesos Background:** WorkManager para recordatorios y notificaciones automáticas.

---

## 4. Planificación y Desglose de Historias de Usuario (14 HU en Total)

### Sprint 1: Arquitectura, Autenticación y Creación de Rutinas

En este sprint se establecen las bases del proyecto, el acceso de usuarios y la lógica inicial de rutinas.

* **HU-1: Autenticación y seguridad inicial**.
* *Épica:* Autenticación.
* *Descripción:* Como usuario quiero registrarme e iniciar sesión para acceder a la app.
* *Criterios de aceptación:* Registro con correo y contraseña; Login válido; Opción con Google; Mensajes de error.


* **HU-2: Selección de objetivos de entrenamiento**.
* *Épica:* Creación de Rutinas.
* *Descripción:* Como usuario quiero seleccionar mi objetivo (hipertrofia, fuerza o mixto) para definir mi entrenamiento.
* *Criterios de aceptación:* Se muestran 3 opciones; Solo una seleccionable; Se guarda la selección.


* **HU-3: Configuración de entrenamiento**.
* *Épica:* Creación de Rutinas.
* *Descripción:* Como usuario quiero configurar mi nivel y frecuencia para personalizar la rutina.
* *Criterios de aceptación:* Selección de nivel; Selección de días por semana; Selección de duración.


* **HU-4: Generación de rutina**.
* *Épica:* Creación de Rutinas.
* *Descripción:* Como usuario quiero generar y guardar una rutina para utilizarla en mis entrenamientos.
* *Criterios de aceptación:* Se genera rutina con ejercicios; Se puede visualizar; Se guarda en Firebase; Se marca como activa.


* **HU-5: Implementación de MVVM**.
* *Épica:* Arquitectura.
* *Descripción:* Como desarrollador quiero implementar MVVM para estructurar el sistema.
* *Criterios de aceptación:* Separación de capas; Uso de ViewModel; Uso de corrutinas.



### Sprint 2: Ejecución de Entrenamientos, Historial y Dashboards Básicos

En este sprint el usuario ya puede entrenar activamente, registrar sus métricas y ver un análisis básico.

* **HU-6: Inicio de entrenamiento**.
* *Épica:* Entrenamiento en Vivo.
* *Descripción:* Como usuario quiero iniciar mi entrenamiento diario para registrar mi actividad.
* *Criterios de aceptación:* Se muestra día correspondiente; Botón de inicio; Acceso a ejercicios.


* **HU-7: Registro de entrenamiento**.
* *Épica:* Entrenamiento en Vivo.
* *Descripción:* Como usuario quiero registrar reps y peso para guardar mi rendimiento.
* *Criterios de aceptación:* Entrada de datos por serie; Guardado correcto; Asociación con rutinaId.


* **HU-8: Visualización de historial**.
* *Épica:* Historial.
* *Descripción:* Como usuario quiero ver mi historial para revisar entrenamientos pasados.
* *Criterios de aceptación:* Lista por fecha; Filtrado por rutina; Detalle de ejercicios.


* **HU-9: Visualización de Dashboard básico**.
* *Épica:* Dashboard Básico.
* *Descripción:* Como usuario quiero ver gráficos para analizar mi progreso.
* *Criterios de aceptación:* Gráfico de líneas (peso); Gráfico de barras (volumen); Datos correctos.


* **HU-10: Consumo de API con Retrofit**.
* *Épica:* Integración API.
* *Descripción:* Como sistema quiero consumir una API de ejercicios para mostrar información adicional.
* *Criterios de aceptación:* Uso de Retrofit; Visualización de datos del ejercicio.


* **HU-11: Implementación de recordatorios**.
* *Épica:* Notificaciones.
* *Descripción:* Como usuario quiero recibir recordatorios para no olvidar entrenar.
* *Criterios de aceptación:* Notificaciones diarias; Uso de WorkManager; Configuración de hora.



### Sprint 3: Inteligencia Artificial, Sensores y Analítica Avanzada

Este sprint dota a la aplicación de características inteligentes, automatización e integración con el hardware.

* **HU-12: Chatbot de entrenamiento**.
* *Épica:* IA Chat.
* *Descripción:* Como usuario quiero consultar un chat para resolver dudas de entrenamiento.
* *Criterios de aceptación:* Interfaz tipo chat; Envío de mensajes; Respuesta desde API.


* **HU-13: Medición de pasos**.
* *Épica:* Sensores.
* *Descripción:* Como usuario quiero ver mis pasos diarios para medir mi actividad.
* *Criterios de aceptación:* Lectura de sensor; Conteo diario; Actualización automática.


* **HU-14: Visualización de Dashboard avanzado**.
* *Épica:* Dashboard Avanzado.
* *Descripción:* Como usuario quiero ver métricas avanzadas para evaluar mi consistencia.
* *Criterios de aceptación:* Gráfico circular (cumplimiento); Comparación semanal; Datos consistentes.