<script type="text/javascript">
    function callBackEmploymentRecord(json) {
        if (json.success) {
            reloadEmployeeMainData(json.data,false);
        }
    }
</script>


<el:validatableForm callBackFunction="callBackEmploymentRecord"
                    name="employmentRecordForm" controller="employmentRecord" action="update">
    <el:hiddenField name="id" value="${employmentRecord?.id}" />
    <g:render template="/employmentRecord/form" model="[isEmployeeDisabled : isEmployeeDisabled ?: params.isEmployeeDisabled,
                                                           employmentRecord:employmentRecord]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>