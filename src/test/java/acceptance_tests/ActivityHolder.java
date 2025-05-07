package acceptance_tests;

import dtu.timemanager.domain.Activity;
import dtu.timemanager.domain.Project;

public class ActivityHolder {
        private Activity activity;
        private String oldDate;

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
    }
