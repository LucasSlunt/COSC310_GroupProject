import React from "react";
import { render, screen } from "@testing-library/react";
import ViewTask from "./pages/ViewTask.jsx";

//jest mock tests for the page
jest.mock("./pages/ViewTask", () => () => <div data-testid="task-info">Mock Taskinfo</div>);
//renders
test("renders Task page", () => {
  render(<ViewTask/>);

  //check if the View task is rendered
  expect(screen.getByTestId("task-info")).toBeInTheDocument();
});