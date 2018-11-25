<el:formGroup>
    <el:textField name="id" size="6" class=""
                  label="${message(code: 'employeeEvaluation.id.label', default: 'id')}"
                  value=""/>
    <g:render template="/employee/wrapper" model="[isSearch   : true,
                                                   withOutForm: true,
                                                   size       : 6]"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="militaryRank"
            action="autocomplete"
            name="militaryRank.id"
            label="${message(code: 'militaryRank.label', default: 'militaryRank')}"/>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="evaluationCriterium"
                     action="autocomplete" name="evaluationResult.id"
                     label="${message(code: 'employeeEvaluation.evaluationResult.label', default: 'evaluationResult')}"/>
</el:formGroup>

<el:formGroup>
    <el:decimalField name="evaluationSum" size="6" class=""
                     label="${message(code: 'employeeEvaluation.evaluationSum.label', default: 'evaluationSum')}"/>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="evaluationTemplate"
                     action="autocomplete" name="evaluationTemplate.id"
                     label="${message(code: 'employeeEvaluation.evaluationTemplate.label', default: 'evaluationTemplate')}"/>
</el:formGroup>

<el:formGroup>
    <el:range type="date" name="fromDate" size="6" class="" setMinDateFromFor="toDate"
              label="${message(code: 'employeeEvaluation.fromDate.label', default: 'fromDate')}"/>
    <el:range type="date" name="toDate" size="6" class=""
              label="${message(code: 'employeeEvaluation.toDate.label', default: 'toDate')}"/>
</el:formGroup>


<g:if test="${!isList}">
    <el:formGroup>
        <el:select valueMessagePrefix="EnumRequestStatus" from="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.values()}"
                   name="requestStatus" size="6" class=""
                   label="${message(code: 'employeeEvaluation.requestStatus.label', default: 'requestStatus')}"/>
    </el:formGroup>
</g:if>

<sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value}">
    <el:formGroup>
        <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                         controller="firm" action="autocomplete"
                         name="firm.id" label="${message(code:'employee.firm.label',default:'firm')}" />
    </el:formGroup>
</sec:ifAnyGranted>