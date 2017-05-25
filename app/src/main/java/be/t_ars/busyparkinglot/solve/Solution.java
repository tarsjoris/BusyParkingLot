package be.t_ars.busyparkinglot.solve;

import java.util.LinkedList;

public class Solution {
    private final LinkedList<Step> fSteps = new LinkedList<Step>();

    public class Step {
        private final byte fCar;
        private final boolean fForward;
        private boolean fLimited;

        public Step(byte car, boolean forward) {
            fCar = car;
            fForward = forward;
            fLimited = false;
        }

        public byte getCar() {
            return fCar;
        }

        public boolean isForward() {
            return fForward;
        }

        public boolean isLimited() {
            return fLimited;
        }

        public void setLimited() {
            fLimited = true;
        }

        @Override
        public String toString() {
            return "" + fCar + (fForward ? 'F' : 'B');
        }
    }

    public void appendStep(final byte carNumber, final boolean forward) {
        fSteps.addLast(new Step(carNumber, forward));
    }

    public Step removeFirstStep() {
        if (!fSteps.isEmpty()) {
            return fSteps.removeFirst();
        }
        return null;
    }

    public Step getLastStep() {
        if (!fSteps.isEmpty()) {
            return fSteps.getLast();
        }
        return null;
    }

    public Step removeLastStep() {
        if (!fSteps.isEmpty()) {
            return fSteps.removeLast();
        }
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        int index = 0;
        for (final Step step : fSteps) {
            if (buffer.length() > 0) {
                buffer.append(";");
            }
            if (++index % 10 == 0) {
                buffer.append('\n');
            }
            buffer.append(step.toString());
        }
        return buffer.toString();
    }
}
