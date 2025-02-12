<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8"/>
    <title>Builds</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <style>
      @import url("https://fonts.googleapis.com/css2?family=Roboto:ital,wght@0,100..900;1,100..900&display=swap");
      @import url("https://fonts.googleapis.com/css2?family=Roboto+Mono:ital,wght@0,100..700;1,100..700&family=Roboto:ital,wght@0,100..900;1,100..900&display=swap");
      body {
        margin: 0;
        margin: 0 5%;
        margin-top: 32pt;
        margin-bottom: 64pt;
        font-family: "Roboto", sans-serif;
        background-color: #222;
        color: #d4d4d4;
        font-size: 10pt;
      }
      h1 {
        font-size: 24pt;
      }
      h3 {
        color: #949494;
      }
      .builds {
        display: flex;
        flex-direction: column;
        gap: 8pt;
      }
      .build {
        display: flex;
        flex-direction: column;
        gap: 8pt;
        background-color: #2f2f2f;
        padding: 12px;
        border: 1px solid #484848;
        border-radius: 4pt;
      }
      .build:hover {
        border: 1px solid;
        cursor: pointer;
        transition: 0.2s;
      }
      .items {
        display: flex;
        justify-content: space-between;
        gap: 8pt;
      }
      .item {
        min-width: 10%;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        text-align: left;
      }
      .header {
        font-size: 9pt;
        color: #7a7a7a;
        font-weight: 600;
        border-bottom: 1px solid #484848;
        padding-bottom: 4px;
        margin-bottom: 8px;
      }
      pre {
        margin: 0;
        font-family: "Roboto Mono", monospace;
        font-size: 9pt;
        color: #c8c8c8;
        background-color: #3f3f3f;
        padding: 2pt 3pt;
        border-radius: 2pt;
        white-space: pre-wrap;
      }
      code {
        margin: 0;
        font-family: "Roboto Mono", monospace;
        font-size: 9pt;
        color: #c8c8c8;
        background-color: #3f3f3f;
        padding: 2pt 3pt;
        border-radius: 2pt;
        width:fit-content;
      }
      .status {
        font-weight: 700;
      }
      .status-success {
        color: #23ca41;
      }
      .status-failure,
      .status-error {
        color: #ca4a23;
      }
      .status-pending {
        color: #caab23;
      }
      .logs {
        margin: 0 4pt;
        margin-bottom: 4pt;
      }
    </style>
    <title>Div with Items</title>
  </head>
  <body>
    <h1>Builds</h1>
    <h3>Builds available: ${model.count}</h3>
    <div class="builds">
      <#list model.builds as build>
        <div class="build" onclick="window.location.href = '/builds/${build.hash}'">
          <div class="items">
            <div class="item">
                <div class="header">Commit</div>
                <code class="content">${build.hash}</code>
            </div>
            <div class="item">
                <div class="header">Date</div>
                <div class="content">${build.date}</div>
            </div>
            <div class="item">
                <div class="header">Owner</div>
                <div class="content">${build.owner}</div>
            </div>
            <div class="item">
                <div class="header">Repository</div>
                <div class="content">${build.repository}</div>
            </div>
            <div class="item">
                <div class="header">Branch</div>
                <code class="content">${build.branch}</code>
            </div>
            <div class="item">
                <div class="header">Status</div>
                <div class="status ${build.statusStyle}">${build.status}</div>
            </div>
          </div>
        </div>
      </#list>
    </div>
  </body>
</html>