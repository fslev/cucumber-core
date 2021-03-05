Feature: Date feature

  Scenario: Match two time dates
    Then Check period from "2018-02-03 01:00:00" to "2019-02-03 01:00:00" is 1 YEARS using date time pattern yyyy-MM-dd HH:mm:ss
    Then Check period from "2018-02-03 01:00:00" to "2019-02-02 12:01:10" is 364 DAYS using date time pattern yyyy-MM-dd HH:mm:ss
    Then Check period from "2019-02-03 01:02:12" to "2019-02-03 23:59:10" is 22 HOURS using date time pattern yyyy-MM-dd HH:mm:ss
    Given param expected="116"
    Then Check period from "2019-02-03 22:02:12" to "2019-02-03 23:59:10" is #[expected] MINUTES using date time pattern yyyy-MM-dd HH:mm:ss
    Given param expected="58"
    Then Check period from "2019-02-03 23:58:12" to "2019-02-03 23:59:10" is #[expected] SECONDS using date time pattern yyyy-MM-dd HH:mm:ss
    Then Check period from "2019-02-03 01:02:12" to "2019-02-03 23:59:10" doesn't match 36 HOURS using date time pattern yyyy-MM-dd HH:mm:ss
    # Use zone
    Then Check period from "2019-02-03 23:58:12+0200" to "2019-02-04 01:59:10+0300" is 1 HOURS using date time pattern yyyy-MM-dd HH:mm:ssZ

  Scenario: Match two dates
    Then Check period from "2019-01-03" to "2019-02-03" is 1 MONTHS using date pattern yyyy-MM-dd
    Then Check period from "2018-01-03" to "2019-02-03" is 1 YEARS using date pattern yyyy-MM-dd
    Then Check period from "2018-02-03" to "2019-02-03" is 365 DAYS using date pattern yyyy-MM-dd
    Then Check period from "2020-02-03" to "2021-02-03" is 1 YEARS using date pattern yyyy-MM-dd
    Then Check period from "2020-02-03" to "2021-02-03" is 366 DAYS using date pattern yyyy-MM-dd
    Then Check period from "2020-02-03" to "2021-02-03" doesn't match 36 DAYS using date pattern yyyy-MM-dd

  Scenario: Format date times from current date
    Given date param currentTime="now PLUS 0 YEARS" with format pattern=yyyy-MM-dd HH:mm:ss
    And date param years="now PLUS 15 YEARS" with format pattern=yyyy-MM-dd HH:mm:ss
    And date param days="now PLUS 15 DAYS" with format pattern=yyyy-MM-dd HH:mm:ss
    And date param months="now PLUS 15 MONTHS" with format pattern=yyyy-MM-dd HH:mm:ss
    And date param minutes="now PLUS 15 MINUTES" with format pattern=yyyy-MM-dd HH:mm:ss

    Then Check period from "#[currentTime]" to "#[years]" is 15 YEARS using date time pattern yyyy-MM-dd HH:mm:ss
    Then Check period from "#[currentTime]" to "#[days]" is 15 DAYS using date time pattern yyyy-MM-dd HH:mm:ss
    Then Check period from "#[currentTime]" to "#[months]" is 15 MONTHS using date time pattern yyyy-MM-dd HH:mm:ss
    Then Check period from "#[currentTime]" to "#[minutes]" is 15 MINUTES using date time pattern yyyy-MM-dd HH:mm:ss

  Scenario: Format dates from current date
    Given date param currentDate="now PLUS 0 YEARS" with format pattern=yyyy-MM-dd
    And date param futureDateYears="now PLUS 15 YEARS" with format pattern=yyyy-MM-dd
    And date param futureDateDays="now PLUS 1 DAYS" with format pattern=yyyy-MM-dd
    And date param pastDateDays="now MINUS 31 DAYS" with format pattern=yyyy-MM-dd

    Then Check period from "#[currentDate]" to "#[futureDateYears]" is 15 YEARS using date pattern yyyy-MM-dd
    And Check period from "#[currentDate]" to "#[futureDateDays]" is 1 DAYS using date pattern yyyy-MM-dd
    And Check period from "#[currentDate]" to "#[pastDateDays]" is -31 DAYS using date pattern yyyy-MM-dd
    And Check period from "#[pastDateDays]" to "#[currentDate]" is 31 DAYS using date pattern yyyy-MM-dd


  Scenario: Format dates from custom date time
    Given date param currentTime="from 2020-04-28 18:05:58 PLUS 0 YEARS" with format pattern=yyyy-MM-dd HH:mm:ss
    And date param years="from 2020-04-28 18:05:58 PLUS 1 YEARS" with format pattern=yyyy-MM-dd HH:mm:ss
    And date param months="from 2020-04-28 18:05:58 PLUS 1 MONTHS" with format pattern=yyyy-MM-dd HH:mm:ss
    And date param days="from 2020-04-28 18:05:58 PLUS 1 DAYS" with format pattern=yyyy-MM-dd HH:mm:ss
    And date param minutes="from 2020-04-28 18:05:58 PLUS 1 MINUTES" with format pattern=yyyy-MM-dd HH:mm:ss
    And date param seconds="from #[currentTime] PLUS 2 SECONDS" with format pattern=yyyy-MM-dd HH:mm:ss
    And date param secondsBefore="from #[minutes] MINUS 61 SECONDS" with format pattern=yyyy-MM-dd HH:mm:ss

    Then Check period from "#[currentTime]" to "#[years]" is 1 YEARS using date time pattern yyyy-MM-dd HH:mm:ss
    Then Check period from "#[currentTime]" to "#[days]" is 1 DAYS using date time pattern yyyy-MM-dd HH:mm:ss
    Then Check period from "#[currentTime]" to "#[months]" is 1 MONTHS using date time pattern yyyy-MM-dd HH:mm:ss
    Then Check period from "#[currentTime]" to "#[minutes]" is 1 MINUTES using date time pattern yyyy-MM-dd HH:mm:ss
    Then Check period from "#[currentTime]" to "#[seconds]" is 0 MINUTES using date time pattern yyyy-MM-dd HH:mm:ss
    Then Check period from "#[currentTime]" to "#[seconds]" is 2 SECONDS using date time pattern yyyy-MM-dd HH:mm:ss
    Then Check period from "#[currentTime]" to "#[secondsBefore]" is 0 YEARS using date time pattern yyyy-MM-dd HH:mm:ss
    Then Check period from "#[currentTime]" to "#[secondsBefore]" is 0 MINUTES using date time pattern yyyy-MM-dd HH:mm:ss
    Then Check period from "#[currentTime]" to "#[secondsBefore]" is -1 SECONDS using date time pattern yyyy-MM-dd HH:mm:ss
