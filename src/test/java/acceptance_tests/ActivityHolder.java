package acceptance_tests;

import dtu.timemanager.domain.Activity;
import dtu.timemanager.domain.Project;
import dtu.timemanager.domain.TimeRegistration;

public class ActivityHolder {
        private Activity activity;
        private String oldDate;
        private TimeRegistration timeRegistration;

        public Activity getActivity() {
            return activity;
        }

        public void setActivity(Activity activity) {
            this.activity = activity;
        }

        public String getOldDate() {
            return oldDate;
        }

        public void setOldDate(String oldDate) {
            this.oldDate = oldDate;
        }

        public void setTimeRegistration(TimeRegistration timeRegistration) {
            this.timeRegistration = timeRegistration;
        }

        public TimeRegistration getTimeRegistration() {
            return timeRegistration;
        }
    }
