Feature: Validating Place API's

  @addplace, @endtoend
  Scenario Outline: Verify if place is getting successfully added
    Given Add place payload with "<name>", "<language>" and <accuracy>
    When user calls "AddplaceAPI" api with "Post" http request
    Then the response status code is 200
    And the "status" in response body is "OK"
    And the "scope" in response body is "APP"
    And I verify placeid maps to "<name>" in "GetplaceAPI"

    Examples:
    | name    | language  | accuracy |
    | Jungan  | Malayalam | 30       |

  @deleteplace, @endtoend
  Scenario: Verify if delete place api is working
    Given Delete payload is ready
    When user calls "DeleteplaceAPI" api with "Post" http request
    Then the response status code is 200
    And the "status" in response body is "OK"

