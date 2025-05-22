# Flask ATDD App

Aplicación Flask simple para practicar pruebas ATDD con Selenium en Java.

## Instalación

```bash
python -m venv venv
.\venv\Scripts\activate
pip install -r requirements.txt
```

## Ejecucion de la página web con Flask

```bash
set FLASK_APP=app
set FLASK_ENV=development
flask run
```

## Ejecución de pruebas con Selenium (Java)

```bash
cd selenium-tests
mvn clean test
```