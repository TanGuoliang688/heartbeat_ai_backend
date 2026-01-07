# Heartbeat AI - Dating Conversation Generator

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.x-green" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Java-17+-blue" alt="Java">
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License">
</p>

## Project Overview

Heartbeat AI is a Spring Boot-based intelligent dating conversation generator service that supports multiple major AI large model APIs, capable of automatically generating appropriate romantic chat responses based on the other person's message and desired tone.

## Core Features

- **Intelligent Conversation Generation**: Generates natural and appropriate romantic chat responses using AI large models
- **Multi-Model Support**: Supports multiple AI service providers including DeepSeek, ERNIE Bot, iFlytek Spark, and Zhipu AI
- **Style Customization**: Customize conversation tone according to different scenarios (e.g., gentle, humorous, romantic)
- **RESTful API**: Provides a clean HTTP interface for easy integration and extension

## Technology Stack

- **Backend Framework**: Spring Boot 3.x
- **AI Integration**: Supports DeepSeek, ERNIE Bot, iFlytek Spark, Zhipu AI
- **Build Tool**: Maven
- **API Documentation**: Swagger/OpenAPI

## Quick Start

### Prerequisites

- JDK 17 or higher
- Maven 3.6+
- Valid API Key for AI service provider

### Installation & Deployment

1. **Clone the project**
   ```bash
   git clone https://gitee.com/major-axis/heartbeat-ai.git
   cd heartbeat-ai
   ```

2. **Configure API Keys**

   Edit `src/main/resources/application.yml` and configure the API keys according to your chosen AI provider:

   ```yaml
   ai:
     provider: deepseek  # Choose AI provider: deepseek/wenxin/xunfei/zhipu
     deepseek:
       api-key: your-deepseek-api-key
       base-url: https://api.deepseek.com
       model-name: deepseek-chat
     wenxin:
       api-key: your-wenxin-api-key
       secret-key: your-wenxin-secret-key
     xunfei:
       app-id: your-xunfei-app-id
       api-key: your-xunfei-api-key
       api-secret: your-xunfei-api-secret
     zhipu:
       api-key: your-zhipu-api-key
   ```

3. **Build the project**
   ```bash
   mvn clean install -DskipTests
   ```

4. **Run the service**
   ```bash
   java -jar target/heartbeat-ai.jar
   ```

## API Usage Guide

### Generate Conversation Response

- **Endpoint**: `POST /api/chat/generate-words`
- **Content-Type**: `application/json`

**Request Parameters**:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| targetMessage | String | Yes | The message received from the other person |
| style | String | Yes | Desired tone/style (e.g., gentle, humorous, romantic) |
| userId | Long | No | User ID |

**Request Example**:
```json
{
  "targetMessage": "I'm so tired after work today",
  "style": "gentle",
  "userId": 1
}
```

**Response Example**:
```json
{
  "code": 200,
  "message": "success",
  "data": "Dear, you must be exhausted~ Let me give you a shoulder massage to relax. Don't forget to rest well!"
}
```

## Project Structure

```
heartbeat-ai/
├── src/main/java/com/tangl/heartbeatai/
│   ├── HeartbeatAiApplication.java       # Application entry point
│   ├── config/
│   │   ├── AiChatConfig.java             # AI service configuration
│   │   └── CorsConfig.java               # CORS configuration
│   ├── controller/
│   │   └── ChatWordsController.java      # Conversation generation controller
│   ├── dto/
│   │   └── ChatWordsRequest.java         # Request data transfer object
│   └── service/
│       ├── AiChatService.java            # AI service interface
│       ├── ChatWordsService.java         # Conversation service
│       └── impl/                         # AI provider implementations
│           ├── DeepSeekAiChatServiceImpl.java
│           ├── WenxinAiChatServiceImpl.java
│           ├── XunfeiAiChatServiceImpl.java
│           └── ZhipuAiChatServiceImpl.java
└── src/main/resources/
    └── application.yml                   # Application configuration file
```

## AI Provider Configuration Details

### DeepSeek
- Website: https://www.deepseek.com
- Features: Fast response, stable generation quality

### ERNIE Bot (Baidu)
- Website: https://yiyan.baidu.com
- Features: Strong Chinese language understanding

### iFlytek Spark
- Website: https://xinghuo.xfyun.cn
- Features: Strong voice interaction capabilities

### Zhipu AI (Zhipu Qingyan)
- Website: https://www.zhipuai.com
- Features: Clear dialogue logic and coherence

## Testing

```bash
# Run unit tests
mvn test

# Run integration tests
mvn test -P integration
```

## Contribution Guidelines

1. Fork this repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contact

- Project URL: https://gitee.com/major-axis/heartbeat-ai
- Author: tangl
- For questions or issues, please open an Issue

---

<p align="center">
  Made with ❤️ by Heartbeat AI Team
</p>