<script type="text/javascript">
    function closeForm() {
        window.location.href = '${createLink(controller:"${request?.requestType?.domain}",action:'list')}';
    }
</script>

<g:set var="entity"
       value="${message(code: 'EnumRequestCategory.'+requestCategory, default: 'Stop Request')}"/>
<g:set var="formTitle"
       value="${message(code: 'default.create.label', args: [entity], default: 'create')}"/>

<el:validatableModalForm title="${formTitle}"
                         width="70%" callBackFunction="closeForm"
                         name="stopRequestForm"
                         controller="request"
                         hideCancel="true"
                         hideClose="true"
                         action="saveOperation">
    <msg:modal/>

    <g:render template="/${request?.requestType?.domain}/stopRequestForm" model="[requestCategory: requestCategory, request: request]" />

    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton onClick="closeForm()" functionName="cancel"/>

</el:validatableModalForm>
