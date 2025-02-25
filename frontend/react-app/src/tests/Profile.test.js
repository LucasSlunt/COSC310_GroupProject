import React from "react";
import { render, screen } from "@testing-library/react";
import Profile from "../pages/Profile";

//jest mock tests for the page
jest.mock("../components/UserInfo", () => () => <div data-testid="user-info">Mock UserInfo</div>);
jest.mock("../components/Teams", () => ({ team }) => (
  <div data-testid={`team-${team.id}`}>{team.name}</div>
));


//rendering tests for the page
test("renders the Profile page with user info, teams, and logout button", () => {
  render(<Profile />);

  //check if the UserInfo component is rendered
  expect(screen.getByTestId("user-info")).toBeInTheDocument();

  //check if the "My teams" heading is displayed
  expect(screen.getByText(/My teams/i)).toBeInTheDocument();

  //check if teams are displayed
  expect(screen.getByTestId("team-1")).toHaveTextContent("The Alphas");
  expect(screen.getByTestId("team-2")).toHaveTextContent("The Nerds");
  expect(screen.getByTestId("team-3")).toHaveTextContent("C++ +");

  //check if the logout button exists
  expect(screen.getByRole("button", { name: /Logout/i })).toBeInTheDocument();
});
