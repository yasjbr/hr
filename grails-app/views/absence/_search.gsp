<el:formGroup>
    <el:textField name="id" size="6" class=" "
                  label="${message(code: 'absence.id.label', default: 'id')}"
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
    <el:select valueMessagePrefix="EnumAbsenceReason"
               from="${ps.gov.epsilon.hr.enums.absence.v1.EnumAbsenceReason.values()}"
               name="absenceReason"
               size="6"
               class="" label="${message(code: 'absence.absenceReason.label', default: 'absenceReason')}"/>
</el:formGroup>
<el:formGroup>

    <div class="form-group ">
            <div class="col-sm-6 pcp-form-control ">
                <label class="col-sm-4 control-label no-padding-right text-left">
    ${message(code: 'absence.numOfDays.label', default: 'numOfDays')}
    </label>

    <div class="col-sm-8">
        <div id="numOfDays" class="input-group">
            <input id="fromNumOfDays"
                   value=""
                   class="form-control isDecimal input-decimal null"
                   type="text"
                   name="fromNumOfDays">
            <span class="input-group-addon">
                <i class="ace-icon icon-sort-numeric-outline"></i>
            </span>
            <input id="toNumOfDays"
                   value=""
                   class="form-control isDecimal input-decimal"
                   type="text"
                   name="toNumOfDays">
        </div>
    </div>
    </div>


    <el:range type="date" size="6" name="fromDate"
              label="${message(code: 'absence.fromDate.label')}"/>
</el:formGroup>
<el:formGroup>
    <el:range type="date" size="6" name="toDate"
              label="${message(code: 'absence.toDate.label')}"/>
    <el:range type="date" size="6" name="noticeDate"
              label="${message(code: 'absence.noticeDate.label')}"/>
</el:formGroup>
<g:if test="${!hideStatusSearch}">
    <el:formGroup>
        <el:select valueMessagePrefix="EnumViolationStatus"
                   from="${ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus.values()}"
                   name="violationStatus" size="6" class=""
                   label="${message(code: 'employeeViolation.violationStatus.label', default: 'violationStatus')}"/>
        <sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value}">
            <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                             controller="firm" action="autocomplete"
                             name="firm.id" label="${message(code: 'firm.label', default: 'firm')}"/>
        </sec:ifAnyGranted>
    </el:formGroup>
</g:if>