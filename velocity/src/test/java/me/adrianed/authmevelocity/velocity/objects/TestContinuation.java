package me.adrianed.authmevelocity.velocity.objects;

import java.util.concurrent.atomic.AtomicBoolean;

import com.velocitypowered.api.event.Continuation;

public class TestContinuation implements Continuation {
    private final AtomicBoolean resumed = new AtomicBoolean(false);

    @Override
    public void resume() {
        resumed.set(true);
    }

    @Override
    public void resumeWithException(Throwable exception) {
        resumed.set(true);
        throw new RuntimeException("Resumed with exception", exception);
    }

    public boolean resumed() {
        return resumed.get();
    }
    
}
