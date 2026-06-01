Feature: Validating Place API's

  @addplace @endtoend
  Scenario Outline: Verify if place is getting successfully added
    When I add a place with "<name>", "<language>" and <accuracy>
    Then the response status code is 200
    And the "status" in response body is "OK"
    And the "scope" in response body is "APP"
    And I verify place_id maps to "<name>"

    Examples:
      | name   | language  | accuracy |
      | Jungan | Malayalam | 30       |

  @deleteplace @endtoend
  Scenario: Verify if delete place api is working
    When I delete the place
    Then the response status code is 200
    And the "status" in response body is "OK"