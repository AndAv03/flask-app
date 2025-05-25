package com.atdd;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.concurrent.TimeUnit;

/****************************************/
// Historia de Usuario: Como usuario registrado quiero agregar una nueva tarea
// para mantener un seguimiento de mis pendientes.
//
// Prueba de Aceptación: Verificar que una tarea nueva aparece en la lista
// después de agregarla.
//
// 1. Iniciar sesión con credenciales válidas
// 2. En el dashboard, escribir el título de la tarea en el campo correspondiente
// 3. Hacer clic en "Agregar"
// 4. Verificar que la tarea aparece en la lista de tareas
//
// RESULTADO ESPERADO: La tarea agregada se muestra correctamente en la lista de tareas.
/****************************************/

public class AgregarTareaTest {

    private WebDriver driver;
    private String nombreUsuario;
    private String contraseña;

    @Before
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        
        // Usar un usuario existente o crear uno nuevo para las pruebas
        nombreUsuario = "testuser";
        contraseña = "123";
    }

    @Test
    public void agregarNuevaTareaYVerificarEnLista() throws InterruptedException {
        // Paso 1: Iniciar sesión
        driver.get("http://localhost:5000/login");
        
        WebElement username = driver.findElement(By.name("username"));
        WebElement password = driver.findElement(By.name("password"));
        
        username.sendKeys(nombreUsuario);
        password.sendKeys(contraseña);
        
        WebElement botonLogin = driver.findElement(By.xpath("//button[text()='Ingresar']"));
        botonLogin.click();
        
        // Verificar que estamos en el dashboard
        Thread.sleep(1000);
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue("No se redirigió al dashboard", currentUrl.contains("/dashboard"));
        
        // Paso 2: Crear un texto simple para la tarea
        String textoTarea = "software";
        
        // Paso 3: Escribir la tarea en el campo de entrada
        WebElement campoTarea = driver.findElement(By.name("task"));
        campoTarea.clear();
        campoTarea.sendKeys(textoTarea);
        
        // Paso 4: Hacer clic en el botón "Agregar"
        WebElement botonAgregar = driver.findElement(By.xpath("//button[text()='Agregar']"));
        botonAgregar.click();
        
        // Esperar a que se actualice la lista (aumentamos el tiempo de espera)
        Thread.sleep(2000);
        
        // Paso 5: Verificar que la tarea aparece en la lista
        // Imprimir todas las tareas encontradas para depuración
        System.out.println("Buscando tarea: " + textoTarea);
        List<WebElement> elementosTarea = driver.findElements(By.cssSelector(".list-group-item"));
        System.out.println("Número de elementos encontrados: " + elementosTarea.size());
        
        for (WebElement elemento : elementosTarea) {
            System.out.println("Texto del elemento: [" + elemento.getText() + "]");
        }
        
        // Intentar con un enfoque más directo para encontrar el texto
        boolean tareaEncontrada = false;
        try {
            // Intentar buscar directamente por el texto
            WebElement elementoTarea = driver.findElement(
                By.xpath("//li[contains(text(),'" + textoTarea + "') or .//*[contains(text(),'" + textoTarea + "')]]"));
            tareaEncontrada = true;
            System.out.println("Tarea encontrada usando XPath alternativo");
        } catch (NoSuchElementException e) {
            System.out.println("No se encontró la tarea usando XPath alternativo");
        }
        
        // Si no encontramos con XPath, intentar con el método original
        if (!tareaEncontrada) {
            for (WebElement elemento : elementosTarea) {
                if (elemento.getText().contains(textoTarea)) {
                    tareaEncontrada = true;
                    System.out.println("Tarea encontrada usando método original");
                    break;
                }
            }
        }
        
        Assert.assertTrue("La tarea agregada no se encontró en la lista", tareaEncontrada);
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
