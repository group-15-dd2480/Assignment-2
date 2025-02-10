<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Build Info</title>
    <style>
        li {
            list-style-type: none;
        }
        .build {
            display: block;
            border: 1px solid black;
            padding: 10px;
            padding-top: 5px;
            padding-bottom: 5px;
            margin: 10px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
            transition: background-color 0.2s;
        }
        .build h2 {
            margin: 0;
            margin-bottom: 5px;
        }
        .build-info p {
            margin: 0;
            margin-bottom: 5px
        }
        .build-info {
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 1px solid black;
        }
        .repo-info p {
            margin: 0;
        }
        .repo-info {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .status {
            display: inline-block;
            padding: 5px;
            border-radius: 5px;
            font-weight: bold;
        }

        .status-success {
            background-color: green;
        }
        .status-failure {
            background-color: red;
        }
        .status-pending {
            background-color: orange;
        }
        .status-error {
            background-color: red;
        }
    </style>
</head>
<body>
    <h1>Build ${build.hash}</h1>
    <ul>
        <li>
            <span class="build">
                <span class="build-info">
                    <p>${build.date}</p>
                    <p class="status ${build.statusStyle}">${build.status}</p>
                </span>
                <span class="repo-info">
                    <p>${build.repository}</p>
                    <p>${build.branch}</p>
                </span>
                <p>${build.message}</p>
                <pre>
                <#list logs as log>
                    ${log}<br>
                </#list>
                </pre>
            </span>
        </li>
    </ul>
</body>
</html>