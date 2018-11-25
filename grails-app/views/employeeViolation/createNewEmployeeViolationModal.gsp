
<g:set var="entity" value="${message(code: 'employeeViolation.entity', default: 'EmployeeViolation List')}" />
<g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'EmployeeViolation List')}" />

<el:validatableModalForm title="${title}" callBackFunction="closeForm"
                         width="70%"
                         name="employeeViolationForm"
                         callLoadingFunction="performPostActionWithEncodedId"
                         controller="employeeViolation"
                         hideCancel="true"
                         hideClose="true"
                         action="save">
    <msg:modal/>
    <g:render template="/employeeViolation/form" model="[employeeViolation:employeeViolation]"/>
    <el:formButton functionName="save" isSubmit="true" />
    <el:formButton onClick="closeForm()" functionName="cancel"/>
</el:validatableModalForm>

<script>
    $('#application-modal-main-content').on("hidden.bs.modal", function () {
        var isCreate = $(this).find('#employeeViolationForm').length;
        if (isCreate > 0) {
            viewEmployeeViolation();
        }
    });

    function closeForm() {
        viewEmployeeViolation();
    }

</script>