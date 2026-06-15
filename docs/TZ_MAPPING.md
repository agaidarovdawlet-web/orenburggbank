# Соответствие техническому заданию

| Требование ТЗ | Реализация | Ориентир в коде |
|---|---|---|
| Справочник систем | `SystemsScreen.kt`, `Entities.kt` | `SystemEntity`, `SystemsScreen` |
| Хранение систем и техработ | `DatabaseHelper.kt` | `onCreate`, `migrateToV3` |
| Создание техработы | `CreateWorkScreen.kt`, `WorkUseCases.kt` | `CreateWorkScreen`, `create` |
| Автоматические согласования | `WorkRepository.kt` | `createWork` |
| Все согласовали / отказ | `WorkStatusResolver.kt` | `resolve` |
| Отмена с проверкой прав | `AuthorizationService.kt`, `WorkRepository.kt` | `requireCanCancelBySystem`, `cancelWork` |
| Права по системам | `AuthorizationService.kt` | `requireCanApprove`, `requireCanCancelBySystem` |
| Делегирование заместителю | `DelegationScreen.kt`, `WorkRepository.kt` | `createDelegation` |
| Просмотр списком | `WorksListScreen.kt` | `WorksListScreen` |
| Просмотр на календаре | `CalendarScreen.kt` | `CalendarScreen` |
| Матрица ролей | `MatrixScreen.kt`, `Role.kt` | `MatrixScreen`, `AppRole` |
| Защита маршрутов | `NavGraph.kt` | защищённые `composable` |
| Аутентификация | `AuthService.kt`, `LoginScreen.kt` | `login`, `LoginScreen` |
| Информация об авторе | `AboutScreen.kt`, `SplashScreen.kt`, `LoginScreen.kt` | соответствующие экраны |
| Тесты требований | `app/src/test/` | `AuthorizationServiceTest`, `WorkStatusResolverTest`, `WorkValidatorTest` |

Номера строк могут изменяться после форматирования; в таблице указаны
стабильные имена файлов и объявлений для быстрого перехода в IDE.
