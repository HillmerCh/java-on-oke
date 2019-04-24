# Deploying  Java Applications on Oracle Cloud 

This repository how to use Docker and Kubernetes with Java Applications. 

Requirements 

* Docker: https://www.docker.com/products/docker-desktop
* Maven: https://maven.apache.org
* Kubectl: https://kubernetes.io/docs/tasks/tools/install-kubectl/
* Oracle Kubernetes Engine OKE: https://cloud.oracle.com/home

## The Java Application


The javaee-cafe is the basic Java EE 8 application used throughout the Docker and Kubertenes demos. It is a simple CRUD application. It uses Maven and Java EE 8 (JAX-RS, EJB, CDI, JPA, JSF, Bean Validation).

You can use any Maven capable IDE such as NetBeans. We use Open Liberty but you should be able to use any Java EE 8 compatible application server such as WildFly or Payara. We use Postgres but you can use any relational database such as MySQL.

The application is composed of:

- **A RESTFul service*:** protocol://hostname:port/javaee-cafe/rest/coffees

	- **_GET by Id_**: protocol://hostname:port/javaee-cafe/rest/coffees/{id} 
	- **_GET all_**: protocol://hostname:port/javaee-cafe/rest/coffees
	- **_POST_** to add a new element at: protocol://hostname:port/javaee-cafe/rest/coffees
	- **_DELETE_** to delete an element at: protocol://hostname:port/javaee-cafe/rest/coffees/{id}

- **A JSF Client:** protocol://hostname:port/javaee-cafe/index.xhtml

## Docker


* Open a console. Add maven to PATH:

	```
	export PATH=/Library/Maven/apache-maven-3.5.0/bin/:$PATH
	```

* Compile and Package the application via maven:

	```
	mvn package -f javaee-cafe/pom.xml 
	```

* Copy the .war file into docker directory:
	```
	cp javaee-cafe/target/javaee-cafe.war docker/
	```

* Build a Docker image tagged `javaee-cafe` issuing the command:
	```
	docker build -t javaee-cafe docker/.
	```
	
	> **Note:** If your Docker instance is running on Linux/Windows before to build the Docker image it is necessary to open the file server/server.xml and edit the serverName="docker.for.mac.localhost" of the dataSource to serverName="localhost"
	
### Deploying the application with Docker
	
* Make sure Docker is running. Open a console.

* Enter the following command and wait for the database to come up fully.
	```
	docker run -it --rm --name javaee-cafe-db -v pgdata:/var/lib/postgresql/data -p 5432:5432 postgres
	```
 
* To run the newly built image, use the command:
	```
	docker run -it --rm -p 9080:9080 javaee-cafe
	```

* Wait for Open Liberty to start and the application to deploy sucessfully (to stop the application and Liberty, simply press Control-C).

* Once the application starts, you can test the REST service at the URL: [http://localhost:9080/javaee-cafe/rest/coffees](http://localhost:9080/javaee-cafe/rest/coffees) or via the JSF client at [http://localhost:9080/javaee-cafe/index.xhtml](http://localhost:9080/javaee-cafe/index.xhtml).


## Kubernetes

### Pushing the Docker image to Docker hub

* Copy the .war file into kubernetes directory:
	```
	cp javaee-cafe/target/javaee-cafe.war kubernetes/
	```

* Log in to Docker Hub using the docker login command:
   ```
   docker login
   ```
* Build a Docker image and push the image to Docker Hub:
   ```
   docker build -t <your Docker Hub account>/javaee-cafe:<your docker image version> .
   docker push <your Docker Hub account>/javaee-cafe:<your docker image version>
   ```

### Deploying the Application on Oracle Kubernetes Engine
  
* Open a terminal. 

* List the containers running in a Cluster:
   ```
   kubectl get pods
   ```
   
* Start the proxy to the Kubernetes API server:
   ```
   kubectl proxy
   ```
   
* Once the Oracle Kubernetes Engine Console starts, you can login at the URL: [http://localhost:8001/api/v1/namespaces/kube-system/services/https:kubernetes-dashboard:/proxy/#!/login](http://localhost:8001/api/v1/namespaces/kube-system/services/https:kubernetes-dashboard:/proxy/#!/login) 
   
* Deploy postgres with a persistent volume claim with the following command:
   ```
   kubectl create -f postgres.yml
   ```

* Get the pod for Postgres:
   ```
   kubectl get pods
   ```
   
* Connect to Postgres:
   ```
   kubectl exec -it postgres-<VER POD> -- psql -U postgres
   ```

* On Postgres and run the command `\dt` to see the tables, '\q' to logout Postgres
  
   
* Create a config map with the hostname of Postgres:
   ```
   kubectl create configmap hostname-config --from-literal=postgres_host=$(kubectl get svc postgres -o jsonpath="{.spec.clusterIP}")
   ``` 

   
* Replace the `<your Docker Hub account>` value with your account name in `kubernetes/javaee-cafe.yml` file, then deploy the application:
   ```
   kubectl create -f kubernetes/javaee-cafe.yml
   ```

* Get the External IP address of the Service, then the application will be accessible at `http://<External IP Address>:9080/javaee-cafe`:
   ```
   kubectl get svc javaee-cafe
   ```
   > **Note:** It may take a few minutes for the load balancer to be created.


* Scale your application:
   ```
   kubectl scale deployment javaee-cafe --replicas=3
   ```
   
## Deleting the Resources
* Delete the Java EE deployment:
   ```
   kubectl delete -f kubernetes/javaee-cafe.yml
   ```

* Delete the hostname config map:
   ```
   kubectl delete cm hostname-config
   ```

* Delete Postgres:
   ```
   kubectl delete -f kubernetes/postgres.yml
   ```