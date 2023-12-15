cd gateway
call mvnw clean package -DskipTests
docker build -t balintjeszenszky/adsite:gateway-latest .
cd ../userservice
call mvnw clean package -DskipTests
docker build -t balintjeszenszky/adsite:userservice-latest .
cd ../advertisementservice
call gradlew clean build -x test
docker build -t balintjeszenszky/adsite:advertisementservice-latest .
cd ../imageprocessingservice
call mvnw clean package -DskipTests
docker build -t balintjeszenszky/adsite:imageprocessingservice-latest .
cd ../web-scraping-service
docker build -t balintjeszenszky/adsite:webscraperservice-latest .
cd ../notificationservice
docker build -t balintjeszenszky/adsite:notificationservice-latest .
cd ../chat-service
docker build -t balintjeszenszky/adsite:chatservice-latest .
cd ../bidservice
call gradlew clean build -x test
docker build -t balintjeszenszky/adsite:bidservice-latest .
cd ../classified-advertisement-site-frontend
call npm install
call npm run openapi:generate
call npm run graphql:generate
call npm run build
docker build -t balintjeszenszky/adsite:frontend-latest .
