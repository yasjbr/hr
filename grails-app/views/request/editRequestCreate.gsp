<script type="text/javascript">
    function closeForm() {
        window.location.href = '${createLink(controller:"${request?.requestType?.domain}",action:'list')}';
    }
</script>

<g:set var="entity"
       value="${message(code: 'EnumRequestCategory.'+requestCategory, default: 'Edit Request')}"/>
<g:set var="formTitle"
       value="${message(code: 'default.create.label', args: [entity], default: 'create')}"/>

<el:validatableModalForm title="${formTitle}"
                         width="70%" callBackFunction="closeForm"
                         name="editRequestForm"
                         controller="request"
                         hideCancel="true"
                         hideClose="true"
                         action="saveOperation">
    <msg:modal/>

    <g:render template="/${request?.requestType?.domain}/editRequestForm" model="[requestCategory: requestCategory, request: request,
                                                                                  hideInterval:true, hideEmployeeInfo:true]"/>

    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton onClick="closeForm()" functionName="cancel"/>

</el:validatableModalForm>
