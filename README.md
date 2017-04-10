# Prueba Datastreaming para la Universidad de Sevilla

### Paso 1: Enciende el servidor ZooKeeper contenido dentro de Kafka
```
$ bin/zookeeper-server-start.sh config/zookeeper.properties
```

### Paso 2: Enciende Kafka
```
$ bin/kafka-server-start.sh config/server.properties
```

### Paso 3: Añade la librería adjuntada a la carpeta resources.

Arrastrar y soltar si se usa un IDE como Intellij.

### Paso 4: Ejecuta el consumer
 
Como argumentos se requiere "todos" o una id que se corresponda con el campo sourceId de alguno de los vehículos.

En el primer modo se procesan todos los vehículos, en el otro solo el vehículo cuyo sourceId coincida con el buscado.

### Paso 5: Esperar resultados

Los resultados serán mostrados a medida que se obtengan, siendo

```
Guardando F:
<Coordenadas>
Guardando S
<Coordenadas>
```

Los puntos inicial y final (casa/trabajo).

Cuando para un coche se obtengan F y S, el sistema comenzara a recopilar la ruta de un punto a otro, mostrándola finalmente en un formato compatible con CSV y la plataforma de OpenData que usaremos para mostrar los puntos recogidos.

### Consulta de rutas finales

Como extra no solo se han obtenido los puntos pertenecientes a las ruta sino que se han exportado a una plataforma de datos abiertos para su uso y visualización.

[Portal Ogoov - 5 Rutas](http://demo.ogoov.com/en/dataset/?id=rutas)

#### Uso:
Entrar en una ruta, nombre basado en los primeros 5 digitos de las sourceId
(Consultar coordenadas extraidas)
Seleccionar en el menu VIEWS
Hacer click en la vista "Recorrido idCoche - Mapa"
(Consultar puntos que trazan la ruta en el mapa)

Se agarece a Viavansi, la empresa Sevillana que creo dicho portal, el permitirnos subir los datos a ahí. 

### Consideraciones

Como Kafka una vez subscrito a un hilo sigue escuchando incluso cuando nuestro consumer no está siendo ejecutado, se necesita descartar todas las tramas recibidas antes de la ejecución del programa.

Una manera sencilla de hacer esto es ejecutar, esperar a que se procesen dichas tramas, detener y nuevamente ejecutar.

