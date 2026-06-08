package Resources;

import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;

import static io.restassured.RestAssured.given;

/**
 * Thread-safe cache for an auth token.
 * Fetches once, reuses until near expiry, refreshes only when needed.
 * Safe under parallel execution (Item 1) via double-checked locking.
 */
public class TokenManager {

    private static final Logger log = LoggerFactory.getLogger(TokenManager.class);

    // volatile => a write by one thread is immediately visible to all others.
    private static volatile String cachedToken;
    private static volatile Instant expiryTime;

    // Single lock so only ONE thread fetches a new token at a time.
    private static final Object lock = new Object();

    // Refresh slightly BEFORE real expiry so a request never goes out with a
    // token that dies mid-flight.
    private static final long EXPIRY_BUFFER_SECONDS = 30;

    public static String getToken() throws IOException {
        // Fast path (no lock): if token is still valid, return it.
        if (isTokenValid()) {
            return cachedToken;
        }
        // Slow path: token missing/expired -> only one thread fetches.
        synchronized (lock) {
            // Double-check: another thread may have refreshed while we waited.
            if (isTokenValid()) {
                return cachedToken;
            }
            refreshToken();
            return cachedToken;
        }
    }

    private static boolean isTokenValid() {
        return cachedToken != null
                && expiryTime != null
                && Instant.now().isBefore(expiryTime.minusSeconds(EXPIRY_BUFFER_SECONDS));
    }

    private static void refreshToken() throws IOException {
        log.info("Token missing or expired - fetching a new one");

        // ============================================================
        // TODO: YOU FILL THIS IN with your real auth API.
        // The Places practice API doesn't use tokens, so this is the
        // skeleton you wire to whatever auth endpoint you actually have.
        //
        // Example shape (adapt to your API):
        //
        // Response resp = given()
        //         .baseUri(Utils.getGlobalProperty("authUrl"))
        //         .contentType("application/json")
        //         .body("{ \"username\": \"" + Utils.getGlobalProperty("authUser") + "\","
        //             + " \"password\": \"" + Utils.getGlobalProperty("authPass") + "\" }")
        //         .when()
        //         .post("/oauth/token");
        //
        // // 1) extract the token
        // cachedToken = resp.jsonPath().getString("access_token");
        //
        // // 2) set expiry from the response if available...
        // long expiresIn = resp.jsonPath().getLong("expires_in");   // seconds
        // expiryTime = Instant.now().plusSeconds(expiresIn);
        //
        // // ...or hardcode a known lifetime if the API doesn't return one:
        // // expiryTime = Instant.now().plusSeconds(3600);
        //
        // log.info("New token acquired, expires at {}", expiryTime);
        // ============================================================

        throw new UnsupportedOperationException(
                "refreshToken() not implemented yet - wire it to your auth API");
    }

    /** Force a refresh on next getToken() - e.g. after a 401 mid-suite. */
    public static void invalidate() {
        synchronized (lock) {
            cachedToken = null;
            expiryTime = null;
            log.info("Token cache invalidated");
        }
    }
}