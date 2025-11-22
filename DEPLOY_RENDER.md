# 🚀 Desplegar Spring Boot en Render

## 📋 Pasos para desplegar:

### 1️⃣ Preparar el repositorio

Asegúrate de que tu código esté en GitHub:

```bash
git add .
git commit -m "Configuración para Render"
git push origin master
```

### 2️⃣ Crear servicio en Render

1. Ve a [Render.com](https://render.com) e inicia sesión
2. Click en **"New +"** → **"Web Service"**
3. Conecta tu repositorio de GitHub: `Amoncadico/tesisAlejandra`
4. Selecciona la carpeta `Back`

### 3️⃣ Configurar el servicio

En Render, configura lo siguiente:

**Configuración básica:**
- **Name**: `spring-boot-api` (o el nombre que prefieras)
- **Region**: Oregon (u otra región cercana)
- **Branch**: `master`
- **Root Directory**: Deja vacío (o `Back` si tu repo tiene múltiples proyectos)
- **Runtime**: **Docker**
- **Dockerfile Path**: `./Dockerfile`
- **Docker Build Context Directory**: `./`
- **Instance Type**: **Free**

### 4️⃣ Variables de entorno

En la sección **Environment**, agrega estas variables:

| Variable | Valor |
|----------|-------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://aws-1-us-east-2.pooler.supabase.com:6543/postgres` |
| `SPRING_DATASOURCE_USERNAME` | `postgres.ifnjxpnmspjrmoqyqxku` |
| `SPRING_DATASOURCE_PASSWORD` | `24052002` |
| `JWT_SECRET` | `======================BezKoder=Spring===========================` |
| `JWT_EXPIRATION_MS` | `3600000` |

### 5️⃣ Configurar CORS

⚠️ **IMPORTANTE**: Una vez desplegado, necesitas actualizar el CORS para permitir tu frontend.

Cuando tengas la URL de tu backend desplegado (ej: `https://tu-app.onrender.com`), actualiza los controladores para incluir esa URL en `@CrossOrigin`.

**Opción A - Configuración global (Recomendado):**

Crea un archivo `CorsConfig.java` en `src/main/java/com/config/`:

```java
package com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList(
            "http://localhost:4200",
            "https://tu-frontend.vercel.app"  // Reemplaza con tu URL de frontend
        ));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
```

**Opción B - Variable de entorno:**

Agrega en Render una variable:
- `ALLOWED_ORIGINS`: `http://localhost:4200,https://tu-frontend.vercel.app`

Y actualiza `application.properties`:
```properties
cors.allowed-origins=${ALLOWED_ORIGINS:http://localhost:4200}
```

### 6️⃣ Deploy

Click en **"Create Web Service"** y Render comenzará a construir tu aplicación.

El proceso tomará unos 5-10 minutos la primera vez.

### 7️⃣ Verificar el despliegue

Una vez desplegado, tu API estará disponible en:
```
https://tu-app.onrender.com
```

Prueba los endpoints:
- `GET https://tu-app.onrender.com/api/test/all`
- `POST https://tu-app.onrender.com/api/auth/signin`

## ⚠️ Notas importantes

1. **Plan Free de Render**: El servicio se "duerme" después de 15 minutos de inactividad. La primera petición tardará ~30 segundos en despertar.

2. **Base de datos**: Ya está configurada con Supabase usando Session Pooler (IPv4).

3. **Logs**: Puedes ver los logs en tiempo real desde el dashboard de Render.

4. **Health Check**: Render verificará `/api/test/all` para confirmar que la app está funcionando.

## 🔧 Solución de problemas

### Error de conexión a la BD:
- Verifica que las variables de entorno estén bien configuradas
- Confirma que Supabase permita conexiones desde la IP de Render

### Error de CORS:
- Actualiza el `@CrossOrigin` con la URL de tu frontend
- O implementa la configuración global de CORS

### Build fallido:
- Revisa los logs en Render
- Verifica que `pom.xml` esté correcto
- Asegúrate de que Java 17 esté especificado

## 📱 Conectar con el frontend

En tu aplicación Angular, actualiza la URL del API:

```typescript
// environment.prod.ts
export const environment = {
  production: true,
  apiUrl: 'https://tu-app.onrender.com/api'
};
```

---

¿Necesitas ayuda con algún paso específico?
