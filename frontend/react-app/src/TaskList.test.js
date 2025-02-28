import TaskList from "./TaskList";
import fakeData from "./fakeTaskData.json"
import { render, screen } from '@testing-library/react';
import React from "react";
//ensures task list renders with all data
test('renders TaskList', () => {
    render(<TaskList />);
    fakeData.forEach(element => {
      expect(screen.getByText(element.name)).toBeInTheDocument();
    });
    
  });
  