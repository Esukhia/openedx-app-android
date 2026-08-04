//Injection to intercept completion state for xBlocks
$(document).on("ajaxSuccess", function(event, request, settings) {
    var url = settings.url || "";
    // Problems never publish completion; submitting an answer through
    // problem_check is what marks them complete on the LMS.
    var completionPublished = url.includes("publish_completion") &&
        request.responseText.includes("ok");
    var problemSubmitted = url.includes("problem_check");
    if (completionPublished || problemSubmitted) {
        javascript:window.callback.completionSet();
    }
});
