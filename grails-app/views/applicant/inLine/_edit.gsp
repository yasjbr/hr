<script type="text/javascript">
    function callBackContactInfo(json) {
        if (json.success) {
            %{--if (json.data && json.data.person && json.data.person.id) {--}%
                %{--$("#personId").val(json.data.person.id);--}%
                %{--var newOption = new Option("${contactInfo?.person?.localFullName}", "${contactInfo?.person?.id}", true, true);--}%
                %{--$('#personId').append(newOption);--}%
                %{--$('#personId').trigger('change');--}%

            %{--} else if (json.data && json.data.organization && json.data.organization.id) {--}%
                %{--$("#organizationId").val(json.data.organization.id);--}%
                %{--var newOption = new Option("${contactInfo?.organization?.descriptionInfo?.localName}", "${contactInfo?.organization?.id}", true, true);--}%
                %{--$('#organizationId').append(newOption);--}%
                %{--$('#organizationId').trigger('change');--}%
            %{--}--}%
            var clickedButton = $('button[formButtonClicked="true"]').first();
            if (clickedButton.attr("closeModal")) {
                renderInLineList();
            }
        }
    }

</script>

<el:validatableResetForm
                         name="applicantForm"
                         controller="interview"
                         action="addApplicantToInterview"   callBackFunction="callBackContactInfo">
    <el:hiddenField name="withRemotingValues" value="true" />
    <el:hiddenField name="interviewIds" id="interviewIds" value="${params.interviewId}" />
    <el:hiddenField name="hasNoInterview" id="hasNoInterview" value="true" />

    <g:render template="/applicant/dataTable"
              model="[isInLineActions:true,title:tabList,entity:entity,domainColumns:'DOMAIN_TAB_COLUMNS']"/>
    <div class="clearfix form-actions text-center">

        <btn:createButton class="btn btn-sm btn-info2" label="${tabTitle}" isSubmit="true">
            <i class="icon-plus"></i>
        </btn:createButton>
    </div>

</el:validatableResetForm>




<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>
