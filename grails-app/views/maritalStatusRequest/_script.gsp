<script type="text/javascript">

    %{-- used to save new request --}%
    function successCreateRequest(json) {
        if (json.success) {
            window.location.href = "${createLink(controller: 'maritalStatusRequest',action: 'list')}";
        }
    }
</script>