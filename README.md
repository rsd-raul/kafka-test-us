# Prueba Datasreaming para la Universidad de Sevilla

### Paso 1: Enciende el servidor ZooKeeper contenido dentro de Kafka
```
$ bin/zookeeper-server-start.sh config/zookeeper.properties
```

### Paso 2: Enciende Kafka
```
$ bin/kafka-server-start.sh config/server.properties
```

### Paso 3: Añade la libreria adjuntada a la carpeta resources.

Arrastrar y soltar si se usa un IDE como Intellij.

### Paso 4: Ejecuta el consumer
 
Como argumentos se requere "todos" o una id que se corresponda con el campo sourceId de alguno de los vehiculos.

En el primer modo se procesan todos los vehiculos, en el otro solo el vehiculo cuyo sourceId coincida con el buscado.

### Paso 5: Esperar resultados

Los resultados serán mostrados a medida que se obtengan, siendo

```
Guardando F:
<Coordenadas>
Guardando S
<Coordenadas>
```
Los puntos inicial y final (casa/trabajo).

Cuando para un coche se obtengan F y S, el sistema comenzara a recopilar la ruta de un punto a otro, mostrandola finalmente en un formato compatible con CSV y la plataforma de OpenData que usaremos para mostrar los puntos recogidos.

### Consideraciones

Como kafka una vez subscrito a un hilo sigue escuchando incluso cuando nuestro consumer no está siendo ejecutado, se necesita descartar todas las tramas recibidas antes de la ejecucion del programa.

Una manera sencilla de hacer esto es ejecutar, esperar a que se procesen dichas tramas, detener y nuevamente ejecutar.
