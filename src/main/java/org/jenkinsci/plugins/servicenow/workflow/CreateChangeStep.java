package org.jenkinsci.plugins.servicenow.workflow;

import hudson.Extension;
import hudson.model.Item;
import hudson.model.Run;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.jenkinsci.plugins.servicenow.ResponseContentSupplier;
import org.jenkinsci.plugins.servicenow.ServiceNowExecution;
import org.jenkinsci.plugins.servicenow.model.ServiceNowConfiguration;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

public class CreateChangeStep extends AbstractServiceNowStep {

    @DataBoundConstructor
    public CreateChangeStep(ServiceNowConfiguration serviceNowConfiguration, String credentialsId) {
        super(serviceNowConfiguration, credentialsId, null);
    }

    @Override
    public StepExecution start(StepContext context) {
        return new Execution(context, this);
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public String getFunctionName() {
            return "serviceNow_createChange";
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return Collections.emptySet();
        }
    }

    public static final class Execution extends SynchronousNonBlockingStepExecution<ResponseContentSupplier> {

        private transient CreateChangeStep step;

        Execution(@Nonnull StepContext context, @Nonnull CreateChangeStep step) {
            super(context);
            this.step = step;
        }

        @Override
        protected ResponseContentSupplier run() throws Exception {
            ServiceNowExecution exec = ServiceNowExecution.from(step, getProject());

            CloseableHttpResponse response = exec.createChange();
            return new ResponseContentSupplier(ResponseContentSupplier.ResponseHandle.STRING, response);
        }

        Item getProject() throws IOException, InterruptedException {
            return getContext().get(Run.class).getParent();
        }

        private static final long serialVersionUID = 1L;

    }

}
