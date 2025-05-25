package com.atdd;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.net.URL;

public class EliminarTareaTest {

    private WebDriver driver;
    private String baseUrl;

    @Before
    public void setUp() throws Exception {
        WebDriverManager.chromedriver().setup();
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capabilities);
        driver.manage().window().maximize();
        baseUrl = System.getenv().getOrDefault("FLASK_BASE_URL", "http://localhost:5000");
    }

    @Test
    public void eliminarTareaExistenteYConfirmarDesaparicion() throws InterruptedException {
        // Paso 1: Iniciar sesión (registrar usuario si es necesario)
        driver.get(baseUrl + "/register");
        String usuario = "usuario" + System.currentTimeMillis();
        String clave = "clave123";
        driver.findElement(By.name("username")).sendKeys(usuario);
        driver.findElement(By.name("password")).sendKeys(clave);
        driver.findElement(By.xpath("//button[text()='Registrarse']")).click();
        Thread.sleep(1000);
        driver.findElement(By.name("username")).sendKeys(usuario);
        driver.findElement(By.name("password")).sendKeys(clave);
        driver.findElement(By.xpath("//button[text()='Ingresar']")).click();
        Thread.sleep(1000);

        // Paso 2: Asegurarse de que hay al menos una tarea visible (si no, agregar una)
        String tituloTarea = "Tarea para eliminar " + System.currentTimeMillis();
        WebElement inputTarea = driver.findElement(By.name("task"));
        inputTarea.sendKeys(tituloTarea);
        driver.findElement(By.xpath("//button[text()='Agregar']")).click();
        Thread.sleep(1000);

        // Paso 3: Hacer clic en el botón de "Eliminar" junto a la tarea
        WebElement botonEliminar = driver.findElement(By.xpath("//li[contains(.,'" + tituloTarea + "')]//a[contains(@class,'btn-outline-danger')]"));
        botonEliminar.click();
        Thread.sleep(1000);

        // Paso 5: Esperar que se actualice la lista
        Thread.sleep(1000);

        // Paso 6: Verificar que la tarea ya no está visible en la lista
        boolean tareaPresente = driver.findElements(By.xpath("//li[contains(.,'" + tituloTarea + "')]")).size() > 0;
        Assert.assertFalse("La tarea eliminada aún está presente en la lista", tareaPresente);
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}